package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.poc.callback.ChatRoomCallback;
import com.randmcnally.bb.poc.callback.EmptyCallback;
import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.database.ChannelDao;
import com.randmcnally.bb.poc.database.DaoSession;
import com.randmcnally.bb.poc.network.ServiceFactory;
import com.randmcnally.bb.poc.fragment.ChannelFragment;
import com.randmcnally.bb.poc.restservice.OpenFireApiService;
import com.randmcnally.bb.poc.util.FileUtil;

import java.util.ArrayList;
import java.util.List;


public class ChannelPresenterImpl implements ChatRoomCallback.ChatRoomCallbackListener{
    Context context;
//    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;
//    ChannelDao channelDao;
    private OpenFireApiService apiService;

    public ChannelPresenterImpl(ChannelFragment view) {
        this.context = view.getContext();
        this.mainView = view;
        apiService = ServiceFactory.createOpenFireAPIService();
        String uniqueID = FileUtil.getDeviceUID(context);


        apiService = ServiceFactory.createOpenFireAPIService();
        apiService.createUser(new UserRequest(uniqueID, uniqueID)).enqueue(new EmptyCallback(new EmptyCallback.EmptyCallbackListener() {
            @Override
            public void onCreateError(String message) {
                showToast(message);
            }
        }));
        loadData();
    }

    private void showToast(final String message){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message , Toast.LENGTH_LONG).show();
            }
        });

    }

    public void loadData() {
        /**
         * The code below allows you to save the channel you already create
         */
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "ptt-db", null);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
//        channelDao = daoSession.getChannelDao();
//        channels = channelDao.loadAll();

        apiService.getChatRooms(ServiceFactory.GROUPCHAT_SERVICE).enqueue(new ChatRoomCallback(this));

        /**
         * This is only for the Demo
         */
//        channels = new ArrayList<>();
//        Channel demoChannel = new Channel();
//        demoChannel.setName("Rand McNally");
//        demoChannel.setStreamName("rand_mcnally");
//        channels.add(demoChannel);

    }

    public List<Channel> getChannels() {
        return channels;
    }

    @Override
    public void notifyChatRoomResponse(List<Channel> channels) {
        this.channels = channels;
        mainView.updateUI();

    }

    /**
     * Add Channel allows you to save the String in the ptt-db Sqlite Database
     * @return
     */
//    public void addChannel(String text) {
//        Channel channel = new Channel();
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
