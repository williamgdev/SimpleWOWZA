package com.randmcnally.bb.poc.restservice;


import com.randmcnally.bb.poc.dto.red5pro.LiveStreamResponse;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface Red5ProApiService {

    @GET("applications/{appname}/streams/{streamname}")
    Call<LiveStreamResponse> getLiveStreamStatistics(@Path("appname") String appName,
                                                     @Path("streamname") String streamName,
                                                         @Query("accessToken") String securityToken);

    @GET("applications/{appname}/media")
    Call<RecordedFileResponse> getRecordedFiles(@Path("appname") String appName,
                                                @Query("accessToken") String securityToken);

//    @POST("live_streams")
//    Call<LiveStreamResponse> createLiveStream(@Body JSONObject jsonStream);

}
