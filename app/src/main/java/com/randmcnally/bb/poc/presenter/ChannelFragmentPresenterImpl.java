package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.network.OpenFireApiManager;
import com.randmcnally.bb.poc.fragment.ChannelFragment;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.view.BaseView;

import java.util.List;

public class ChannelFragmentPresenterImpl implements ChannelFragmentPresenter{
    Context context;
//    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;
//    ChannelDao channelDao;
    private OpenFireApiManager apiManager;

    public ChannelFragmentPresenterImpl(ChannelFragment view) {
        this.context = view.getContext();
        this.mainView = view;

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

    @Override
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


        String uniqueID = FileUtil.getDeviceUID(context);

        apiManager = OpenFireApiManager.getInstance();
        apiManager.createUser(new UserRequest(uniqueID, uniqueID), new OpenFireApiManager.CreateUserApiListener() {
            @Override
            public void onSuccess(String s) {
                showToast(s);
            }

            @Override
            public void onError(String s) {
                showToast(s);
            }
        });

        apiManager.getChatRooms(new OpenFireApiManager.ChatRoomApiListener() {
            @Override
            public void onSuccess(List<Channel> channels) {
                setChannels(channels);
            }

            @Override
            public void onError(String s) {

            }
        });

        /**
         * This is only for the Demo
         */
//        channels = new ArrayList<>();
//        Channel demoChannel = new Channel();
//        demoChannel.setName("Rand McNally");
//        demoChannel.setStreamName("rand_mcnally");
//        channels.add(demoChannel);

    }

    @Override
    public List<Channel> getChannels() {
        return channels;
    }

    @Override
    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        mainView.updateUI();

    }

    @Override
    public void attachView(BaseView baseView) {

    }

    @Override
    public void detachView() {

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
