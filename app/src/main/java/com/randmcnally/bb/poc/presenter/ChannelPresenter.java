package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.view.ChannelView;

public interface ChannelPresenter extends BasePresenter<ChannelView> {
    void loadData();
    String getMessage();

    void startBroadcast();

    void stopBroadcast();

    boolean isBroadcasting();

    boolean isPreparing();

    boolean isPlaying();
}
