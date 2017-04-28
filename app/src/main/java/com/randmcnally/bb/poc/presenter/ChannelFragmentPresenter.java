package com.randmcnally.bb.poc.presenter;

import android.os.Bundle;

import com.randmcnally.bb.poc.dto.openfire.ChatRoom;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;
import java.util.List;

public interface ChannelFragmentPresenter extends BasePresenter<ChannelFragmentView>{

    void getFavoriteChannels();

    void getChannels();

    void updateChannel();

    void getMissedMessages(HashMap<MultiUserChat, List<Message>> openFireServer, List<ChatRoom> chatRooms);

    void updateChannelMissedMessages(Channel channel);

    void setChannels(List<Channel> channels);

    Bundle getBundle(Channel channel);

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);

    void setOpenFireServer(OpenFireServer openFireServer);
}
