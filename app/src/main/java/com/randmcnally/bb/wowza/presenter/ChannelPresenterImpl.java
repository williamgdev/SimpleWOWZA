package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.database.ChannelDao;
import com.randmcnally.bb.wowza.database.DaoMaster;
import com.randmcnally.bb.wowza.database.DaoSession;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.fragment.ChannelFragment;
import com.randmcnally.bb.wowza.view.fragment.DialogTextFragment;

import java.util.List;


public class ChannelPresenterImpl implements MainPresenter{
    Context context;
    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;
    ChannelDao channelDao;

    public ChannelPresenterImpl(Context context) {
        this.context = context;
        loadData();
    }

    @Override
    public void loadData() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "ptt-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        channelDao = daoSession.getChannelDao();

        Channel channel = new Channel();
        channel.setName("First");
        channelDao.insert(channel);
        
        updateChannelItems();

    }

    private void updateChannelItems() {
        channels = channelDao.loadAll();
        mainView.updateUI();
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = (ChannelFragment) mainView;
    }

    @Override
    public void detachView() {
        mainView = null;
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
}
