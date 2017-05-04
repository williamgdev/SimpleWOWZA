package com.randmcnally.bb.poc.interactor;

import com.randmcnally.bb.poc.dto.openfire.ChatRoom;
import com.randmcnally.bb.poc.dto.openfire.ChatRoomResponse;
import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.GroupChat;
import com.randmcnally.bb.poc.network.OpenFireInterceptor;
import com.randmcnally.bb.poc.restservice.OpenFireApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class OpenFireApiInteractor {
    public static final String GROUPCHAT_SERVICE = "randmcnally";
//    public static final String XMPP_DOMAIN = "openfire.test";
//        public static final String HOST_NAME = "192.168.43.212";
    public static final String XMPP_DOMAIN = "ip-172-31-6-205.us-west-2.compute.internal";
    public static final String HOST_NAME = "54.212.192.196";

    public static final String BASE_URL = "http://" + HOST_NAME + ":9090/plugins/restapi/v1/";

    private static OpenFireApiInteractor instance;
    private OpenFireApiService apiService;

    private OpenFireApiInteractor() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OpenFireInterceptor.buildHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        apiService = retrofit.create(OpenFireApiService.class);

    }


    public static OpenFireApiInteractor getInstance() {
        if (instance == null) {
            instance = new OpenFireApiInteractor();
        }
        return instance;
    }

    public void getChatRooms(ChatRoomApiListener listener) {
        apiService.getChatRooms(GROUPCHAT_SERVICE).enqueue(getChatRoomCallback(listener));
    }

    public void createUser(UserRequest userRequest, CreateUserApiListener listener) {
        apiService.createUser(userRequest).enqueue(getCreateUserCallback(listener));
    }

    public void addUserGroupChat(String roomName, String rol, String userName, AddUserToGroupApiListener listener){
        apiService.addUserGroupChat(roomName, rol, userName).enqueue(getUserGroupChatCallback(listener));
    }

    public Callback<Void> getUserGroupChatCallback(final AddUserToGroupApiListener listener){
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case AddUserToGroupApiListener.CREATED:
                        listener.onSuccess("User added to the group successfully");
                        break;
                    default:
                        listener.onError("Create Failed: " + response.message());
                        break;
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage().toString());
            }
        };
    }

    public Callback<Void> getCreateUserCallback(final CreateUserApiListener listener) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case CreateUserApiListener.CREATED:
                        listener.onSuccess("Account created successfully");
                        break;
                    case CreateUserApiListener.SERVER_ERROR:
                        listener.onSuccess("Account already exists");
                        break;
                    default:
                        listener.onError("Create Failed: " + response.message());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage().toString());
            }
        };
    }

    public Callback<ChatRoomResponse> getChatRoomCallback(final ChatRoomApiListener listener){
        return new Callback<ChatRoomResponse>() {
            @Override
            public void onResponse(Call<ChatRoomResponse> call, Response<ChatRoomResponse> response) {
                switch (response.code()) {
                    case ChatRoomApiListener.OK:
                        if (response.body().getChatRooms() != null) {
                            listener.onSuccess(response.body().getChatRooms());
                        }
                        break;
                    default:
                        listener.onError("Error " + response.message());
                        break;
                }
            }

            @Override
            public void onFailure(Call<ChatRoomResponse> call, Throwable t) {
                listener.onError(t.getMessage().toString());
            }
        };
    }


    public interface ChatRoomApiListener extends BaseApiListener {
        void onSuccess(List<ChatRoom> channels);
        void onError(String s);
    }

    public interface CreateUserApiListener extends BaseApiListener{
        void onSuccess(String s);
        void onError(String s);
    }

    public interface AddUserToGroupApiListener extends BaseApiListener{
        void onSuccess(String s);
        void onError(String s);
    }

}
