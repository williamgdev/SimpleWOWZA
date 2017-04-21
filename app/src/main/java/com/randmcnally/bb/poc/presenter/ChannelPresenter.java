package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelView;

public interface ChannelPresenter extends BasePresenter<ChannelView> {
    void loadData();

    String getMessage();

    void startBroadcast();

    void stopBroadcast();

    boolean isBroadcasting();

    void setRed5ProHistory(History red5ProHistory);

    boolean isPreparing();

    boolean isPlaying();

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);

    void setOpenFireServer(OpenFireServer openFireServer);

    int getMissedMessages();
}
