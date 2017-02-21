package com.randmcnally.bb.wowza.callback;

import android.util.Log;

import com.randmcnally.bb.wowza.dto.LiveStreamResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreamStatusCallback implements Callback<LiveStreamResponse> {

    private String TAG = "StreamStatusCallback ->";
    ResultStreamStatusCallback resultCallback;
    public String message;

    public StreamStatusCallback(ResultStreamStatusCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
        if (response.body() != null) {
            if (response.body().getLiveStream() != null) {
                Log.d(TAG, "onResponse: State: " + response.body().getLiveStream().getState());
                if (response.body().getLiveStream().getState().equals("starting")) {
                    resultCallback.notifyStreamStatus(ResultStreamStatusCallback.DONE);
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
    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: error: ", t);
        resultCallback.notifyStreamStatus(ResultStreamStatusCallback.ERROR);
        message = t.getMessage();
    }

    //Notify the result for the Callback
    public interface ResultStreamStatusCallback{
        void notifyStreamStatus(int resultCallback);
        int DONE = 1, ERROR = 0;
    }

}
