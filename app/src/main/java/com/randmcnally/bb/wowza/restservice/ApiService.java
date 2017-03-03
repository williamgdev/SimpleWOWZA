package com.randmcnally.bb.wowza.restservice;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.randmcnally.bb.wowza.dto.AllStreamsResponse;
import com.randmcnally.bb.wowza.dto.StatusResponse;
import com.randmcnally.bb.wowza.dto.TranscorderResponse;

import org.json.JSONObject;

public interface ApiService {

    @GET("live_streams")
    Call<AllStreamsResponse> getAllLiveStreams();

    @GET("live_streams/{id}")
    Call<StatusResponse> getLiveStream(@Path("id") String id);

    @GET("live_streams/{id}/state")
    Call<StatusResponse> getState(@Path("id") String id);

    @PUT("live_streams/{id}/start")
    Call<StatusResponse> _startLiveStream(@Path("id") String id);

    @PUT("live_streams/{id}/stop")
    Call<StatusResponse> stopLiveStream(@Path("id") String id);

    @PUT("live_streams/{id}/reset")
    Call<StatusResponse> resetLiveStream(@Path("id") String id);

    @GET("transcoders/{id}/stats")
    Call<TranscorderResponse> getTranscorderStatus(@Path("id") String id);

//    @POST("live_streams")
//    Call<LiveStreamResponse> createLiveStream(@Body JSONObject jsonStream);

}
