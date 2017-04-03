package com.randmcnally.bb.poc.callback;

import android.util.Log;

import com.randmcnally.bb.poc.dto.openfire.ChatRoomResponse;
import com.randmcnally.bb.poc.model.Channel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomCallback implements Callback<ChatRoomResponse> {
    private static final String TAG = "ChatRoomCallback ->";
    ChatRoomCallbackListener listener;

    public ChatRoomCallback(ChatRoomCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<ChatRoomResponse> call, Response<ChatRoomResponse> response) {
        switch (response.code()){
            case 200:
                if (response.body().getChatRooms() != null)
                    listener.notifyChatRoomResponse(Channel.create(response.body().getChatRooms()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(Call<ChatRoomResponse> call, Throwable t) {
        Log.d(TAG, "onResponse: ");

    }
    public interface ChatRoomCallbackListener{
        void notifyChatRoomResponse(List<Channel> channels);
    }
}
