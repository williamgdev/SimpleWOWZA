package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.database.ChannelDao;
import com.randmcnally.bb.wowza.database.DaoMaster;
import com.randmcnally.bb.wowza.database.DaoSession;
import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.view.ChannelView;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.fragment.ChannelFragment;

import java.util.List;


public class ChannelPresenterImpl {
    Context context;
    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;
    ChannelDao channelDao;

    public ChannelPresenterImpl(ChannelFragment view) {
        this.context = view.getContext();
        this.mainView = view;
        loadData();
    }

    public void loadData() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "ptt-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        channelDao = daoSession.getChannelDao();
        channels = channelDao.loadAll();

    }

    private void updateChannelItems() {
        channels = channelDao.loadAll();
        mainView.updateUI();
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void addChannel(String text) {
        Channel channel = new Channel();
        channel.setName(text);
        channelDao = daoSession.getChannelDao();
        channelDao.insert(channel);
        updateChannelItems();
    }

    public String _getUrlStream() {
        return GoCoderSDK.getUrlStream();
    }
}
