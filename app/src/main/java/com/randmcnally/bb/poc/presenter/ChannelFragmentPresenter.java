package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.List;

public interface ChannelFragmentPresenter extends BasePresenter<ChannelFragmentView>{
    void registerDevice();
    void getChannels();
    void setChannels(List<Channel> channels);
}
