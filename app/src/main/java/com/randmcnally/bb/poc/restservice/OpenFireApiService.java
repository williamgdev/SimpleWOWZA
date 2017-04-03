package com.randmcnally.bb.poc.restservice;

import com.randmcnally.bb.poc.dto.openfire.ChatRoomResponse;
import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.dto.openfire.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenFireApiService {

    @POST("users")
    Call<Void> createUser(@Body UserRequest user);

    @GET("users/{username}")
    Call<UserResponse> getUser(@Path("username") String userName);

    @GET("chatrooms")
    Call<ChatRoomResponse> getChatRooms(@Query("servicename") String service);

    @POST("chatrooms/{roomName}/{roles}/{name}")
    Call<Void> addUserGroupChat(@Path("roomName") String roomName, @Path("roles") String rol, @Path("name") String userName);
}
