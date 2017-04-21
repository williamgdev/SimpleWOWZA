package com.randmcnally.bb.poc.presenter;

import android.os.Bundle;

import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.List;

public interface ChannelFragmentPresenter extends BasePresenter<ChannelFragmentView>{

    void getFavoriteChannels();

    void getChannels();

    void updateChannel();

    void getMissedMessages(OpenFireServer openFireServer, Channel channel);

    void updateMissedMessages(Channel channel);

    void setChannels(List<Channel> channels);

    Bundle getBundle(Channel channel);

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);

    void setOpenFireServer(OpenFireServer openFireServer);
}
