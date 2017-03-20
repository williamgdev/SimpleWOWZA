package com.randmcnally.bb.poc.network;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Red5ProInterceptor implements Interceptor {

    private String accessToken;

    public Red5ProInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(createRequest(chain));
    }

    public Request createRequest(Chain chain) {
        return chain.request()
                .newBuilder()
                .addHeader("accessToken", accessToken)
                .build();
    }

    public static OkHttpClient buildHttpClient(String accessToken){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();
        httpClientBuilder.addInterceptor(new Red5ProInterceptor(accessToken));

        return httpClientBuilder.build();
    }
}

