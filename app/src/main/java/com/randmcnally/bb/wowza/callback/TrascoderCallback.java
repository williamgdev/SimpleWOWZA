package com.randmcnally.bb.wowza.callback;

import android.util.Log;

import com.randmcnally.bb.wowza.dto.TranscorderResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrascoderCallback implements Callback<TranscorderResponse> {
    private ListenerTranscoderCallback listener;

    public TrascoderCallback(ListenerTranscoderCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<TranscorderResponse> call, Response<TranscorderResponse> response) {
        if (response.body() != null) {
            if (response.body().getMeta() != null) {
                listener.notifyTranscoderStatus(false, response.body().getMeta().getMessage());
            } else if (response.body().getTranscoder() != null) {
                if (response.body().getTranscoder().getConnected() != null) {
                    if (response.body().getTranscoder().getConnected().getValue().equals("Yes"))
                        listener.notifyTranscoderStatus(true, response.body().getTranscoder().getConnected().getText());
                    else
                        listener.notifyTranscoderStatus(false, response.body().getTranscoder().getConnected().getText());
                }
            }
        }
    }

    @Override
    public void onFailure(Call<TranscorderResponse> call, Throwable t) {
        listener.notifyTranscoderStatus(false, t.getMessage());
    }

    public interface ListenerTranscoderCallback{
        void notifyTranscoderStatus(boolean isConnected, String message);
    }
}
