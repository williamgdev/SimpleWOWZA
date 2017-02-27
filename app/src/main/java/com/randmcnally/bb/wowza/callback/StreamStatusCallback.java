package com.randmcnally.bb.wowza.callback;

import android.provider.Settings;
import android.util.Log;

import com.randmcnally.bb.wowza.dto.StatusResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreamStatusCallback implements Callback<StatusResponse>{

    private String TAG = "StreamStatusCallback ->";
    ListenerStreamStatusCallback resultCallback;
    public String message;

    public StreamStatusCallback(ListenerStreamStatusCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
        if (response.body() != null) {
            if (response.body().getLiveStream() != null) {
                switch (response.body().getLiveStream().getState()) {
                    case "starting":
                        Log.d(TAG, "onResponse: Starting");
                        resultCallback.listenerStreamStatus(ListenerStreamStatusCallback.WAITING);
                        break;
                    case "started":
                        resultCallback.listenerStreamStatus(ListenerStreamStatusCallback.DONE);
                        break;
                    case "stopping":
                    case "stopped":
                        resultCallback.listenerStreamStatus(ListenerStreamStatusCallback.STOP);
                        break;
                }
                message = response.body().getLiveStream().getState();
            }
            if (response.body().getMeta() != null) {
                message = response.body().getMeta().getMessage();
                Log.d(TAG, "onResponse: State: " + response.body().getMeta().getMessage());
            }
        }
    }

    @Override
    public void onFailure(Call<StatusResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: error: ", t);
        resultCallback.listenerStreamStatus(ListenerStreamStatusCallback.ERROR);
        message = t.getMessage();
    }

    //Notify the result for the Callback
    public interface ListenerStreamStatusCallback {
        void listenerStreamStatus(int resultCallback);
        int DONE = 1, ERROR = -1, WAITING = 8, STOP = 0;
    }

}
