package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.model.Channel;

import java.util.List;

public interface ChannelFragmentPresenter extends BasePresenter{
    void loadData();
    List<Channel> getChannels();
    void setChannels(List<Channel> channels);
}
