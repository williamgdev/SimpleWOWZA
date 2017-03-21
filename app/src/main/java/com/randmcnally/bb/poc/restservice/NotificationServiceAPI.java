package com.randmcnally.bb.poc.restservice;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationServiceAPI {

    @POST("push")
    Call<PushyAPI.PushyPushRequest> sendNotification(@Body PushyAPI.PushyPushRequest pushRequest, @Query("api_key") String secretAPIkey);
}
