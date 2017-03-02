package com.randmcnally.bb.wowza.util;

import android.os.Handler;
import android.os.Looper;

import com.randmcnally.bb.wowza.callback.M3UAvailableCallback;
import com.randmcnally.bb.wowza.network.ServiceFactory;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class RTSPInteractor {
    boolean isPlaying;
    private static RTSPInteractor ourInstance = new RTSPInteractor();

    public static RTSPInteractor getInstance() {
        return ourInstance;
    }

    private RTSPInteractor() {
    }

    public void checkRTSPUrl(final M3UAvailableCallback listener, final String m3u8Url){
        final OkHttpClient httpClient = new OkHttpClient();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                httpClient.newCall(ServiceFactory.getM3UFileRequest(m3u8Url)).enqueue(listener);
//            }
//        }, 500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                httpClient.newCall(ServiceFactory.getM3UFileRequest(m3u8Url)).enqueue(listener);
            }
        }, 500);
    }

    public void stopPlay() {
        isPlaying = false;
    }

    public void startPlay() {
        isPlaying = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
