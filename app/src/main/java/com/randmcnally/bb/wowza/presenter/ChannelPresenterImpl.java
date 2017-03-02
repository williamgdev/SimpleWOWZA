package com.randmcnally.bb.wowza.presenter;

import android.content.Context;

import com.randmcnally.bb.wowza.callback.AllStreamCallback;
import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.database.ChannelDao;
import com.randmcnally.bb.wowza.database.DaoSession;
import com.randmcnally.bb.wowza.dto.LiveStream;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.ChannelUtil;
import com.randmcnally.bb.wowza.fragment.ChannelFragment;

import java.util.List;


public class ChannelPresenterImpl implements AllStreamCallback.ListenerAllStream{
    AllStreamCallback allStreamsCallback;
    Context context;
    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;
    ChannelDao channelDao;
    private ApiService apiService;

    public ChannelPresenterImpl(ChannelFragment view) {
        this.context = view.getContext();
        this.mainView = view;
        apiService = ServiceFactory.createAPiService();
        allStreamsCallback = new AllStreamCallback(this);

        loadData();
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

        /**
         * the code bellow retreive all channels using the /LiveStreams Rest Call
         */
        apiService.getAllLiveStreams().enqueue(allStreamsCallback);

        /**
         * This is only for the Demo
         */
//        channels = new ArrayList<>();
//        Channel demoChannel = new Channel();
//        demoChannel.setName("Rand McNally");
//        channels.add(demoChannel);

    }

    private void updateChannelItems() {
        /**
         * use this code when you are using thr Local Database
         */
//        channels = channelDao.loadAll();

        mainView.updateUI();
    }

    public List<Channel> getChannels() {
        return channels;
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

    @Override
    public void getResponseLivesStreams(List<LiveStream> liveStreams) {
        channels = ChannelUtil.toChannel(liveStreams);
        updateChannelItems();
    }
}
