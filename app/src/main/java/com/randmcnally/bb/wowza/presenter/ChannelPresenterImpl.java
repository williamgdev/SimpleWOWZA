package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.database.ChannelDao;
import com.randmcnally.bb.wowza.database.DaoMaster;
import com.randmcnally.bb.wowza.database.DaoSession;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.fragment.ChannelFragment;

import java.util.List;


public class ChannelPresenterImpl implements MainPresenter {
    Context context;
    DaoSession daoSession;
    ChannelFragment mainView;
    List<Channel> channels;

    public ChannelPresenterImpl(Context context) {
        this.context = context;

    }

    @Override
    public void loadData() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "ptt-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        updateChannelItems();

    }

    private void updateChannelItems() {
        ChannelDao channelDao = daoSession.getChannelDao();
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
}
