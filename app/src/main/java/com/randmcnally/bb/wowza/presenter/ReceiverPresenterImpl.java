package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.view.MainView;

import java.io.IOException;

public class ReceiverPresenterImpl implements MainPresenter {
    private static final String TAG = "Receiver ->";
    private MediaPlayer mediaPlayer;
    Context context;
    String url;

    public ReceiverPresenterImpl(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void loadData() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void attachView(MainView mainView) {

    }

    @Override
    public void detachView() {

    }


    public void stopListen() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void startListen(){
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }


//
//    public void stopListenRecording() {
//        mediaPlayer.stop();
//        mediaPlayer.release();
//    }
//
//    public void startListenRecording() {
//        mediaPlayer = MediaPlayer.create(context, Uri.parse(savedFile.toString()));
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.start();
//            }
//        });
//    }

}
