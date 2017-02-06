package com.github.williamgdev.examples.simplewowza;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class PlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private CenterLayout centerLayout;
    private String path;
    private String TAG = "PlayerActivity =>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SetUp Vitamio Library
        if(!LibsChecker.checkVitamioLibs(this)) {
            Log.d(TAG, "onCreate: Error Initializing Vitamio...");
            return;
        }
        setContentView(R.layout.activity_player);


        videoView = (VideoView) findViewById(R.id.buffer);
        centerLayout = (io.vov.vitamio.widget.CenterLayout) findViewById(R.id.video_layer);

    }
    public void playFromPath(View view){
        path = "https://c2a38e.entrypoint.cloud.wowza.com/app-2343/ngrp:13e3b911_all/playlist.m3u8";
        centerLayout.setVisibility(View.VISIBLE);

        videoView.setVideoURI(Uri.parse(path));
        videoView.setMediaController(new MediaController(videoView.getContext()));
        videoView.requestFocus();
        videoView.setOnInfoListener(new io.vov.vitamio.MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (videoView.isPlaying()) {
                            videoView.pause();

                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        videoView.start();
                        break;
                }
                return true;
            }
        });
        videoView.setOnBufferingUpdateListener(new io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(io.vov.vitamio.MediaPlayer mp, int percent) {
                Log.d(TAG, "onBufferingUpdate: Percent: " + percent + "%");
            }
        });
        videoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(io.vov.vitamio.MediaPlayer mp) {
                try{
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // optional need Vitamio 4.0
                mp.setPlaybackSpeed(1.0f);
            }
        });
        videoView.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(io.vov.vitamio.MediaPlayer mp, final int what, int extra) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayerActivity.this, "Error: " + what, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });













//        videoView.setMediaController(new MediaController(this));
//        videoView.setVideoURI(Uri.parse(path));
//        videoView.requestFocus();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Play Music", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

//        mediaPlayer = new MediaPlayer();
//        try {
//
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//
//                @Override
//                public void onBufferingUpdate(MediaPlayer mp, int percent) {
//
//                    // TODO Auto-generated method stub
//                    if(mediaPlayer.isPlaying()){
//                        is_playing_firstTime = true;
//                    }else{
//                        System.out.println("MP stopped");
//                    }
//                    System.out.println(percent + "% 0");
//                    if(isFirst_Loading_Complete) {
//                        System.out.println(percent + "% 1");
//                    }
//                    if (percent < 5) {
//                        mediaPlayer.pause();
//                        isStart = true;
//                        if(isFirst_Loading_Complete) {
//                            System.out.println(percent + "% 2");
//                        }
//                    }
//                    else if (percent < 20) {
//                        // loadRateView.setText(percent + "%");
//                        // allows the user to actively aware Play
//                    } else { // > 20%
//                        mediaPlayer.start();
//                        System.out.println(percent + "% 3");
//                        isFirst_Loading_Complete = true;// true because first loading is displayed
////                    mediaPlayer.setBufferSize(5000);
//                        System.out.println(percent + "% 4");
//                    }
//                }
//            });
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mediaPlayer.start();
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "Music Playing now", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            mediaPlayer.prepare();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    mediaPlayer.release();
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "Music Stop", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
////        mediaPlayer.setBufferSize(5000); //set buffer size
//        isFirst_Loading_Complete = false;

    }
}
