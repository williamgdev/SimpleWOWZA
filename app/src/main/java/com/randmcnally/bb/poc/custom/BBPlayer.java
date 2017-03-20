package com.randmcnally.bb.poc.custom;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class BBPlayer {
    private static final String TAG = "BBPlayer ->";
    ListenerBBPlayer listener;
    String rtspUrl;
    private boolean playing;
    private MediaPlayer mediaPlayer;


    public BBPlayer(String rtspUrl, ListenerBBPlayer listener) throws IOException {
        this.listener = listener;
        this.rtspUrl = rtspUrl;
        playing = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(onInfoListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(rtspUrl);

    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        playing = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        listener.onListener(BBPLAYER.STOPPED);
    }

    public void start() throws IOException {
        playing = true;
        listener.onListener(BBPLAYER.PREPARING);
        mediaPlayer.prepareAsync();

    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            listener.onListener(BBPLAYER.PLAYING);
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            listener.onListener(BBPLAYER.AUDIO_STREAM_COMPLETED);
        }
    };

    MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    listener.onListener(BBPLAYER.AUDIO_STREAM_END);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    listener.onListener(BBPLAYER.AUDIO_STREAM_START);
                    break;

                default:
                    listener.onListener(BBPLAYER.INFO_UNKNOWN);
            }
            return true;

        }
    };

    MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                default:
                    listener.onListener(BBPLAYER.ERROR_UNKNOWN);
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

    public enum BBPLAYER{
        PLAYING, AUDIO_STREAM_COMPLETED, AUDIO_STREAM_END, AUDIO_STREAM_START, INFO_UNKNOWN, ERROR_UNKNOWN, STOPPED, PREPARING
    }

    public interface ListenerBBPlayer{
        void onListener(BBPLAYER state);
    }
}
