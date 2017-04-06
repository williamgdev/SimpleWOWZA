package com.randmcnally.bb.poc.view;

import com.randmcnally.bb.poc.model.Channel;

import java.util.ArrayList;
import java.util.List;

public interface ChannelFragmentView extends BaseView{
    void setChannels(List<Channel> channels);
}
