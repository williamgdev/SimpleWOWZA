package com.randmcnally.bb.poc.custom;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;

import java.io.IOException;

public class BBPlayer {
    private static final String TAG = "BBPlayer ->";
    private static String ipAddress;
    ListenerBBPlayer listener;
    String mediaUrl;
    Playlist playlist;
    private boolean playing;
    private MediaPlayer mediaPlayer;
    private boolean playListAdded;
    private VoiceMessage actualVoiceMessage;
    private boolean release;
    private boolean pause;

    public BBPlayer(String url, ListenerBBPlayer listener) throws IOException {
        this.listener = listener;
        this.mediaUrl = url;
        playing = false;
        release = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(onInfoListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);

    }

    public BBPlayer(Playlist playlist, String ipAddress, ListenerPlaylistBBPlayer listener) throws IOException {
        this.ipAddress = ipAddress;
        this.listener = listener;
        this.playlist = playlist;
        playing = false;
        playListAdded = true;
        createMediaPlayer();

    }

    private void createMediaPlayer() throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(onInfoListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        if (!playlist.isEmpty()) {
            actualVoiceMessage = playlist.nextMessage();
            mediaPlayer.setDataSource(Red5ProApiInteractor.getURLStream(actualVoiceMessage.getName(), ipAddress));
            mediaPlayer.prepare();
        }
        release = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        if (playing || pause) {
            playing = false;
            pause = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            release = true;
            listener.onListener(BBPLAYERSTATE.STOPPED);
        }
    }

    public void play() throws IOException {
        playing = true;
        pause = false;
        if (playListAdded){
            listener.onListener(BBPLAYERSTATE.PLAYING);
            mediaPlayer.start();
        } else {
            listener.onListener(BBPLAYERSTATE.PREPARING);
            mediaPlayer.prepareAsync();
        }
    }

    public void pause() {
        mediaPlayer.pause();
        playing = false;
        pause = true;
    }

    private void playNextVoiceMessage() {
        ((ListenerPlaylistBBPlayer) listener).onMessageCompleted(actualVoiceMessage);
        mediaPlayer.release();
        release = true;
        try {
            createMediaPlayer();
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (!playListAdded){
                mp.start();
                listener.onListener(BBPLAYERSTATE.PLAYING);
            }
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (playListAdded) {
                if (!playlist.isEmpty()) {
                    playNextVoiceMessage();
                }
                else{
                    stop();
                    listener.onListener(BBPLAYERSTATE.PLAYLIST_EMPTY);
                    ((ListenerPlaylistBBPlayer) listener).onMessageCompleted(actualVoiceMessage);
                }
            }
            else {
                listener.onListener(BBPLAYERSTATE.AUDIO_STREAM_COMPLETED);
            }
        }
    };

    MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    listener.onListener(BBPLAYERSTATE.AUDIO_STREAM_END);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    listener.onListener(BBPLAYERSTATE.AUDIO_STREAM_START);
                    break;

                default:
                    listener.onListener(BBPLAYERSTATE.INFO_UNKNOWN);
            }
            return true;

        }
    };

    MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                default:
                    listener.onListener(BBPLAYERSTATE.ERROR_UNKNOWN);
                    break;
            }
            stop();
            return true;
        }
    };

    public void forceStop() {
        if (isPlaying())
            mediaPlayer.stop();
    }

    public VoiceMessage getCurrentMessage() {
        return actualVoiceMessage;
    }

    public int getTimeElapsed() {
        if (playing) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public boolean isReleased() {
        return release;
    }

    public enum BBPLAYERSTATE {
        PLAYING, AUDIO_STREAM_COMPLETED, AUDIO_STREAM_END, AUDIO_STREAM_START, INFO_UNKNOWN, ERROR_UNKNOWN, STOPPED, PLAYLIST_EMPTY, PREPARING
    }

    public interface ListenerBBPlayer{
        void onListener(BBPLAYERSTATE state);
    }

    public interface ListenerPlaylistBBPlayer extends ListenerBBPlayer{
        void onMessageCompleted(VoiceMessage voiceMessage);
    }

}
