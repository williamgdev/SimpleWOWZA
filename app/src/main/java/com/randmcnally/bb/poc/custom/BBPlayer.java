package com.randmcnally.bb.poc.custom;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.randmcnally.bb.poc.model.Playlist;

import java.io.IOException;

public class BBPlayer {
    private static final String TAG = "BBPlayer ->";
    ListenerBBPlayer listener;
    String mediaUrl;
    Playlist playlist;
    private boolean playing;
    private MediaPlayer mediaPlayer;
    private boolean playListAdded;

    public BBPlayer(String url, ListenerBBPlayer listener) throws IOException {
        this.listener = listener;
        this.mediaUrl = url;
        playing = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(onInfoListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);

    }

    public BBPlayer(Playlist playlist, ListenerBBPlayer listener) throws IOException {
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
        String url = "";
        if (!playlist.isEmpty()) {
            url = playlist.getOlderMessages().getUrl();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        playing = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        listener.onListener(BBPLAYERSTATE.STOPPED);
    }

    public void play() throws IOException {
        playing = true;
        if (playListAdded){
            mediaPlayer.start();
        } else {
            listener.onListener(BBPLAYERSTATE.PREPARING);
            mediaPlayer.prepareAsync();
        }
    }

    private void playNextVoiceMessage() {
        mediaPlayer.release();
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
                    listener.onListener(BBPLAYERSTATE.MESSAGE_COMPLETE);
                }
                else{
                    stop();
                    listener.onListener(BBPLAYERSTATE.PLAYLIST_EMPTY);
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

    public enum BBPLAYERSTATE {
        PLAYING, AUDIO_STREAM_COMPLETED, AUDIO_STREAM_END, AUDIO_STREAM_START, INFO_UNKNOWN, ERROR_UNKNOWN, STOPPED, MESSAGE_COMPLETE, PLAYLIST_EMPTY, PREPARING
    }

    public interface ListenerBBPlayer{
        void onListener(BBPLAYERSTATE state);
    }

}
