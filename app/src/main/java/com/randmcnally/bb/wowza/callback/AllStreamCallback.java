package com.randmcnally.bb.wowza.callback;

import com.randmcnally.bb.wowza.dto.AllStreamsResponse;
import com.randmcnally.bb.wowza.dto.LiveStream;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllStreamCallback implements Callback<AllStreamsResponse> {
    ListenerAllStream listenerAllStream;
    String message;

    public AllStreamCallback(ListenerAllStream listenerAllStream) {
        this.listenerAllStream = listenerAllStream;
    }

    @Override
    public void onResponse(Call<AllStreamsResponse> call, Response<AllStreamsResponse> response) {
        if (response.body() != null) {
            if (response.body().getMeta() != null){
                message = response.body().getMeta().getMessage();
            }
            if (response.body().getLiveStreams() != null) {
                listenerAllStream.getResponseLivesStreams(response.body().getLiveStreams());
            }
            else {
                listenerAllStream.getResponseLivesStreams(new ArrayList<LiveStream>());
            }
        }

    }

    @Override
    public void onFailure(Call<AllStreamsResponse> call, Throwable t) {
        listenerAllStream.getResponseLivesStreams(new ArrayList<LiveStream>());
        message = t.getMessage();

    }

    public interface ListenerAllStream{
        void getResponseLivesStreams(List<LiveStream> liveStreams);
    }
}
