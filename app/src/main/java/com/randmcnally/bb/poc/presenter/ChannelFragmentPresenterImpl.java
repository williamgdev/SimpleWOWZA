package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.custom.BBGroupChat;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.dto.openfire.ChatRoom;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.HashMap;
import java.util.List;

public class ChannelFragmentPresenterImpl implements ChannelFragmentPresenter {
    private static final String TAG = "ChannelFragmentPresenterImpl ->";
    ChannelFragmentView channelFragmentView;
    List<Channel> channels;
    private OpenFireApiInteractor apiManager;
    private DatabaseInteractor databaseInteractor;
    private OpenFireServer openFireServer;

    @Override
    public void getFavoriteChannels() {
        /**
         * The code below allows you to save the channel you already create
         */
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "ptt-db", null);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
//        channelDao = daoSession.getChannelDao();
//        channels = channelDao.loadAll();


    }

    @Override
    public void getChannels() {
        apiManager = OpenFireApiInteractor.getInstance();
        apiManager.getChatRooms(new OpenFireApiInteractor.ChatRoomApiListener() {
            @Override
            public void onSuccess(List<ChatRoom> chatRooms) {
                setChannels(Channel.create(chatRooms));
                getMissedMessages(openFireServer.getGroupChatHistoryHashMap(), chatRooms);
                updateChannel();
            }

            @Override
            public void onError(String s) {
                showToast(s);
            }
        });
    }

    @Override
    public void updateChannelMissedMessages(final Channel channel) {
        databaseInteractor.readByNameOrCreate(channel.getRoomId(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(result);
                List<VoiceMessage> missedMessages = ChannelUtil.getMissedMessage(channel.getHistory().getVoiceMessages(), VoiceMessage.createFromVoiceMessagelEntity(voiceMessages));
                channel.getHistory().setMissedMessages(Playlist.create(missedMessages));
            }
        });
    }

    @Override
    public void setChannels(List<Channel> channels) {
        this.channels = channels;

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
        Log.d(TAG, "setOpenFireServer: run");
        this.openFireServer = openFireServer;
    }

    @Override
    public void getMissedMessages(final HashMap<String, BBGroupChat> groupChatHistoryHashMap, final List<ChatRoom> chatRooms) {
        Log.d(TAG, "getListenedMessages: Run");

        if (groupChatHistoryHashMap.size() != 0){

            for (Channel channel :
                    channels) {
                if (groupChatHistoryHashMap.containsKey(channel.getRoomId())){
                    channel.setHistory(History.create(groupChatHistoryHashMap.get(channel.getRoomId()).getMessages()));
                    updateChannelMissedMessages(channel);
                }
            }

        }
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
    }

    @Override
    public void detachView() {
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

    /**
     * Add ChannelEntity allows you to save the String in the ptt-db Sqlite Database
     * @return
     */
//    public void addChannel(String text) {
//        ChannelEntity channel = new ChannelEntity();
//        channel.setName(text);
//        channelDao = daoSession.getChannelDao();
//        channelDao.insert(channel);
//        updateChannelItems();
//    }
//
//    @Override
//    public void getResponseLivesStreams(List<LiveStream> liveStreams) {
//        channels = ChannelUtil.toChannel(liveStreams);
//        updateChannelItems();
//    }
}
