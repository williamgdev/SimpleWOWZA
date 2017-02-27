package com.randmcnally.bb.wowza.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WowzaInterceptor implements Interceptor {
    private static final String API_KEY = "hBp76TIjgGJqUKaJkTfmdZ4EHAEKcA3GRQDfM2n2eFrDbjTBLnidstk2jS56321c";
    private static final String ACCESS_KEY = "y0hsLVdvjFemqJnIRodyhsvRXurULfDxEzgivUg41ZAwIVwBeOzppA1C7cfP375e";

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(createRequest(chain));
    }

    public Request createRequest(Chain chain) {
        return chain.request()
                .newBuilder()
                .addHeader("wsc-api-key", API_KEY)
                .addHeader("wsc-access-key", ACCESS_KEY)
                .build();
    }

    public static OkHttpClient buildHttpClient(){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();
        httpClientBuilder.addInterceptor(new WowzaInterceptor());

        return httpClientBuilder.build();
    }
}

