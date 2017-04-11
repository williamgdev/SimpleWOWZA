package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.view.ChannelView;

public interface ChannelPresenter extends BasePresenter<ChannelView> {
    void loadData();

    String getMessage();

    void startBroadcast();

    void stopBroadcast();

    boolean isBroadcasting();

    void setHistory(History history);

    boolean isPreparing();

    boolean isPlaying();

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);
}
