package com.randmcnally.bb.wowza.service;

import com.randmcnally.bb.wowza.model.dto.LiveStreamPojo;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @PUT("live_streams/{id}/start")
    Call<LiveStreamPojo> startLiveStream(@Path("id") String id);

    @PUT("live_streams/{id}/stop")
    Call<LiveStreamPojo> stopLiveStream(@Path("id") String id);

}
