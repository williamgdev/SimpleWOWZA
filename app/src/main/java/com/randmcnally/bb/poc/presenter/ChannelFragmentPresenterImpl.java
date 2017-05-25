package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.custom.BBGroupChat;
import com.randmcnally.bb.poc.dao.ChannelEntity;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.dto.openfire.ChatRoom;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;
import com.randmcnally.bb.poc.model.GroupChat;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;
import java.util.List;

public class ChannelFragmentPresenterImpl implements ChannelFragmentPresenter {
    private static final String TAG = "ChannelFragmentPresenterImpl ->";
    ChannelFragmentView channelFragmentView;
    private List<Channel> channels;
    private OpenFireApiInteractor apiManager;
    private DatabaseInteractor databaseInteractor;
    private OpenFireServer openFireServer;
    private boolean isConnected;

    @Override
    public void getFavoriteChannels() {
        /**
         * The code below allows you to save the channel you already created
         */
//
        databaseInteractor.readChannels(new DatabaseInteractor.DatabaseListener<List<ChannelEntity>>() {
            @Override
            public void onResult(List<ChannelEntity> result) {
                channels = Channel.createFromChannelEntity(result);
                updateChannel();
            }
        });
    }

    @Override
    public void joinToFavoritesChannels() {
        for (int i = 0; i < channels.size(); i++) {
            final int index = i;
            if (channels.get(i).isFavorite()) {
                openFireServer.getGroupChatRoom(channels.get(i).getName(), new OpenFireServer.OpenFireListener<MultiUserChat>() {
                    @Override
                    public void onSuccess(MultiUserChat result) {
                        jointToGroupChat(result, index);

                    }

                    @Override
                    public void onError(String message) {
                        Log.d(TAG, "onError: " + message);
                    }
                });
            }
        }
    }

    private void jointToGroupChat(MultiUserChat result, final int index) {
        openFireServer.joinToGroupChat(result, new OpenFireServer.OpenFireListener<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                /**
                 * TODO Cash the channel on the database
                 */
                Log.d(TAG, "jointToGroupChat: " + channels.get(index).getName());
                channels.get(index).getHistory().setHistory(VoiceMessage.createFromMessages(result));
                if (index == channels.size() - 1) {
                    getMissedMessages(openFireServer.getGroupChatHistoryHashMap());
                }
            }

            @Override
            public void onError(String message) {
                Log.d(TAG, "onError: " + message);
            }
        });
    }

    @Override
    public void updateChannelsFromServer() {
        apiManager = OpenFireApiInteractor.getInstance(((BBApplication) channelFragmentView.getContext().getApplicationContext()).IP_ADDRESS);
        apiManager.getChatRooms(new OpenFireApiInteractor.ChatRoomApiListener() {
            @Override
            public void onSuccess(List<ChatRoom> chatRooms) {
                setChannels(Channel.createFromChatRoom(chatRooms));
                joinToFavoritesChannels();
            }

            @Override
            public void onError(String s) {
                showToast(s);
            }
        });
    }

    @Override
    public void setChannels(List<Channel> channels) {
        this.channels = ChannelUtil.hasChanges(this.channels, channels);
        saveChannelsOnDatabase(channels);

    }

    @Override
    public void saveChannelsOnDatabase(final List<Channel> channels) {
        databaseInteractor.saveChannels(channels, new DatabaseInteractor.DatabaseListener<List<ChannelEntity>>() {
            @Override
            public void onResult(List<ChannelEntity> result) {
                if (result.size() != channels.size()){
                    Log.d(TAG, "saveChannelsOnDatabase: Database Changed, please check your favoritesChannels.");
                    updateChannel();
                }
            }
        });
    }

    @Override
    public Bundle getBundle(Channel channel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("channel", channel);
        return bundle;
    }

    @Override
    public void setDatabaseInteractor(DatabaseInteractor databaseInteractor) {
        this.databaseInteractor = databaseInteractor;
    }

    @Override
    public void setOpenFireServer(final OpenFireServer openFireServer) {
        this.openFireServer = openFireServer;

        if (openFireServer.isConnected()){
            isConnected = true;
        }
        Log.d(TAG, "setOpenFireServer: Connected " + isConnected);
    }

    @Override
    public void setFavoriteChannel(Channel channel) {
        channel.setFavorite(!channel.isFavorite()); // switch boolean in case that was already favorite
        updateChannel();
        if (channel.isFavorite()){
            databaseInteractor.createChannel(channel, new DatabaseInteractor.DatabaseListener<ChannelEntity>() {
                @Override
                public void onResult(ChannelEntity result) {
                    Log.d(TAG, "setFavoriteChannel: Save Channel Favorite: " + result.getName());
                }
            });
        } else {
            databaseInteractor.removeChannel(channel.getName());
            Log.d(TAG, "setFavoriteChannel: Remove Favorite Channel: " + channel.getName());
        }
    }

    @Override
    public void getMissedMessages(final HashMap<String, BBGroupChat> groupChatHistoryHashMap) {
        if (groupChatHistoryHashMap.size() > 0){
            for (Channel channel :
                    channels) {
                if (groupChatHistoryHashMap.containsKey(channel.getName()) && channel.isFavorite()){ //get the missed messages only if is Favorite
                    channel.setHistory(History.create(groupChatHistoryHashMap.get(channel.getName()).getMessages()));
                    ChannelUtil.updateChannelMissedMessages(channel, databaseInteractor);

                    updateChannel();
                    Log.d(TAG, "updateChannelMissedMessages: " + channel.getName() + " - " + channel.getHistory().getMissedMessages().size()  + " missed messages" );

                    saveChannelsOnDatabase(channel);
                }
            }
        }
    }

    private void saveChannelsOnDatabase(Channel channel) {
        databaseInteractor.updateChannel(channel, new DatabaseInteractor.DatabaseListener<ChannelEntity>() {
            @Override
            public void onResult(ChannelEntity result) {
                Log.d(TAG, "saveChannelsOnDatabase: " + result.getName());
            }
        });
    }

    @Override
    public void updateChannel() {
        ((Activity) channelFragmentView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                channelFragmentView.setChannels(channels);
            }
        });
    }

    @Override
    public void attachView(ChannelFragmentView view) {
        this.channelFragmentView = view;
        LocalBroadcastManager.getInstance(channelFragmentView.getContext()).registerReceiver(
                localNotificationReceiver,
                new IntentFilter("RMBB_MISSED_MESSAGE")
        );
    }

    @Override
    public void detachView() {
        LocalBroadcastManager.getInstance(channelFragmentView.getContext()).unregisterReceiver(localNotificationReceiver);
        channelFragmentView = null;
    }

    private void showToast(final String message) {
        ((Activity) channelFragmentView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(channelFragmentView.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }

    private BroadcastReceiver localNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMissedMessages(openFireServer.getGroupChatHistoryHashMap());
            LiveStream streamReceived = (LiveStream) intent.getExtras().getSerializable("live_stream");
            ChannelUtil.notifyMessageMissed(streamReceived, databaseInteractor);
        }
    };
}
