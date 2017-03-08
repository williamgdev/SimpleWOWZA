package com.randmcnally.bb.wowza.custom;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

public class BBPlayer {
    private static final String TAG = "BBPlayer ->";
    ListenerBBPlayer listener;
    String rtspUrl;
    private boolean playing, mute;
    private MediaPlayer mediaPlayer;
    Context context;


    public BBPlayer(Context context, String rtspUrl, ListenerBBPlayer listener) throws IOException {
        this.listener = listener;
        this.rtspUrl = rtspUrl;
        this.context = context;

        if (!LibsChecker.checkVitamioLibs((Activity)context))
            return;

        mediaPlayer = buildPlayer();

    }

    private MediaPlayer buildPlayer() throws IOException {
        MediaPlayer mp = new MediaPlayer(context);
        mp.setOnInfoListener(onInfoListener);
        mp.setOnErrorListener(onErrorListener);
        mp.setOnPreparedListener(onPreparedListener);
        mp.setOnCompletionListener(onCompletionListener);

//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDataSource(rtspUrl);
//        mediaPlayer.setLooping(true);
        playing = false;
        mute = false;
        return mp;
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

    public void start() {
        if (mute){
            /**
             * Check the audio volume in the device.
             */
            mediaPlayer.setVolume(1.0f, 1.0f);
            mute = false;
        }
        else {
            mediaPlayer.prepareAsync();
        }
        playing = true;
        listener.onListener(BBPLAYER.PLAYING);

    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.setPlaybackSpeed(1.0f);
        listener.onListener(BBPLAYER.PLAYING);
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            listener.onListener(BBPLAYER.AUDIO_STREAM_COMPLETED);
            mediaPlayer.release();
            try {
                mediaPlayer = buildPlayer();
                Log.d(TAG, "onCompletion: new Player Created");
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void mute() {
        if (isPlaying()) {
            mediaPlayer.setVolume(0, 0);
            mute = true;
            playing = false;
            listener.onListener(BBPLAYER.STOPPED);
        }
    }

    public enum BBPLAYER{
        PLAYING, AUDIO_STREAM_COMPLETED, AUDIO_STREAM_END, AUDIO_STREAM_START, INFO_UNKNOWN, ERROR_UNKNOWN, STOPPED, PREPARING
    }

    public interface ListenerBBPlayer{
        void onListener(BBPLAYER state);
    }
}
