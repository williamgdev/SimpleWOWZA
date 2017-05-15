package com.randmcnally.bb.poc.presenter;

import android.os.Bundle;

import com.randmcnally.bb.poc.custom.BBGroupChat;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.HashMap;
import java.util.List;

public interface ChannelFragmentPresenter extends BasePresenter<ChannelFragmentView>{

    void getFavoriteChannels();

    void joinToFavoritesChannels();

    void updateChannelsFromServer();

    void updateChannel();

    void getMissedMessages(HashMap<String, BBGroupChat> groupChatHistoryHashMap);

    void updateChannelMissedMessages(Channel channel);

    void setChannels(List<Channel> favoritesChannels);

    void saveChannelsOnDatabase(List<Channel> channels);

    Bundle getBundle(Channel channel);

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);

    void setOpenFireServer(OpenFireServer openFireServer);

    void setFavoriteChannel(Channel channel);
}
