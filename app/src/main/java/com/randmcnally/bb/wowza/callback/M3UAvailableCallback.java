package com.randmcnally.bb.wowza.callback;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class M3UAvailableCallback implements Callback {

    private static final String TAG = "";
    ListenerM3UAvailableCallback listener;

    public M3UAvailableCallback(ListenerM3UAvailableCallback listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        Log.d(TAG, "onFailure: M3u8");
        listener.notifyM3UStatus(ListenerM3UAvailableCallback.M3UStatus.ERROR);
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        if (!response.isSuccessful()) {
            Log.d(TAG, "onResponse: " + response.code());
            listener.notifyM3UStatus(ListenerM3UAvailableCallback.M3UStatus.ERROR);
        } else {
            // do something wih the result
            Log.d(TAG, "onResponse: " + response.toString());
            listener.notifyM3UStatus(ListenerM3UAvailableCallback.M3UStatus.AVAILABLE);
        }
        response.body().close();
    }

    public interface ListenerM3UAvailableCallback{
        void notifyM3UStatus(M3UStatus status);
        enum M3UStatus{
            AVAILABLE, ERROR
        }
    }
}

