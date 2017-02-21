package com.randmcnally.bb.wowza.callback;

import android.util.Log;

import com.randmcnally.bb.wowza.dto.LiveStreamResponse;
import com.randmcnally.bb.wowza.dto.Wowz;
import com.wowza.gocoder.sdk.api.status.WZState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreamStatusCallback implements Callback<LiveStreamResponse> {

    private String TAG = "StreamStatusCallback ->";
    ResultStreamStatusCallback resultCallback;

    public StreamStatusCallback(ResultStreamStatusCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
        if (response.body() != null) {
            if (response.body().getLiveStream() != null) {
                Log.d(TAG, "onResponse: State: " + response.body().getLiveStream().getState());
                if (response.body().getLiveStream().getState().equals("starting"))
                    resultCallback.streamStarted(ResultStreamStatusCallback.DONE);
            }
            if (response.body().getMeta() != null)
                Log.d(TAG, "onResponse: State: " + response.body().getMeta().getMessage());
        }
    }

    @Override
    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: error: ", t);
        resultCallback.streamStarted(ResultStreamStatusCallback.ERROR);
    }

    //Notify the result for the Callback
    public interface ResultStreamStatusCallback{
        void streamStarted(int resultCallback);
        int DONE = 1, ERROR = 0;
    }

}
