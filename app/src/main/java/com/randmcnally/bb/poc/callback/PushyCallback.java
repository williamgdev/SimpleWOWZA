package com.randmcnally.bb.poc.callback;

import com.randmcnally.bb.poc.restservice.PushyAPI;

import retrofit2.Call;
import retrofit2.Response;

public class PushyCallback implements retrofit2.Callback<com.randmcnally.bb.poc.restservice.PushyAPI.PushyPushRequest> {

    private final PushyListener listner;

    public PushyCallback(PushyListener listener) {
        this.listner = listener;
    }

    @Override
    public void onResponse(Call<PushyAPI.PushyPushRequest> call, Response<PushyAPI.PushyPushRequest> response) {
        if (response.code() != 200)
            listner.errorPushy("Error: " + response.message());
    }

    @Override
    public void onFailure(Call<PushyAPI.PushyPushRequest> call, Throwable t) {
        listner.errorPushy("Error: " + t.getMessage());

    }

    public interface PushyListener{
        void errorPushy(String message);
    }
}
