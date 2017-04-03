package com.randmcnally.bb.poc.callback;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmptyCallback implements Callback<Void> {
    private static final String TAG = "EmptyCallback ->";
    EmptyCallbackListener listener;

    public EmptyCallback(EmptyCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        switch (response.code()){
            case 201:
                Log.d(TAG, "onResponse: OK");
                break;
            case 500:
                listener.onCreateError("Account already exists");
                break;
            default:
                listener.onCreateError("Create Failed: " + response.message());
                break;
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
        listener.onCreateError("Create Failed" + t.getMessage());
    }

    public interface EmptyCallbackListener{
        void onCreateError(String message);
    }
}
