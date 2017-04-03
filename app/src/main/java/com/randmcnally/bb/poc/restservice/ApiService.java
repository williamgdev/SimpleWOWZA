package com.randmcnally.bb.poc.restservice;


import com.randmcnally.bb.poc.dto.red5pro.LiveStreamResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @GET("applications/{appname}/streams/{streamname}")
    Call<LiveStreamResponse> getLiveStreamStatistics(@Path("appname") String appName,
                                                     @Path("streamname") String streamName,
                                                         @Query("accessToken") String securityToken);


//    @POST("live_streams")
//    Call<LiveStreamResponse> createLiveStream(@Body JSONObject jsonStream);

}
