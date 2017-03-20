package com.randmcnally.bb.poc.callback;

import android.util.Log;

import com.randmcnally.bb.poc.dto.LiveStreamResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusLiveStreamCallback implements Callback<LiveStreamResponse> {

    private static final String TAG = "StatusLiveStreamCallback ->";
    private final LiveStreamListener listener;

    public StatusLiveStreamCallback(LiveStreamListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
        switch (response.code()){
            case 200:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.CONNECTED, "The Stream is started.");
                break;
            case 404:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.ERROR, "Resource not found.");
                break;
            case 400:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.ERROR, "Bad Request because of incorrect number of arguments, or argument format or the order or arguments.");
                break;
            case 409:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.ERROR, "Invalid operation requested. An operation is requested which cannot be performed given the current state of target resource.");
                break;
            case 500:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.ERROR, "An I/O operation failure occurred.Stream not found in application live.");
                break;
            case 401:
                listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.ERROR, "Unauthorized. The http client is denied access. Authentication information is missing or invalid.");
                break;
        }
    }

    @Override
    public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
        listener.notifyLiveStreamStatus(LiveStreamListener.STATUS.FAILURE, t.getMessage());
    }

    public interface LiveStreamListener{
        enum STATUS{ERROR, FAILURE, CONNECTED}
        void notifyLiveStreamStatus(STATUS status, String message);
    }
}
