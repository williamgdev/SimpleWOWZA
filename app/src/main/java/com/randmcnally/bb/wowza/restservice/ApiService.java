package com.randmcnally.bb.wowza.restservice;


import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.randmcnally.bb.wowza.dto.LiveStreamResponse;

public interface ApiService {

    @PUT("live_streams/{id}/start")
    Call<LiveStreamResponse> startLiveStream(@Path("id") String id);

    @PUT("live_streams/{id}/stop")
    Call<LiveStreamResponse> stopLiveStream(@Path("id") String id);

}
