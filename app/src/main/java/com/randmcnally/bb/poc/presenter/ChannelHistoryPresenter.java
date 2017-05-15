package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

import java.util.List;

public interface ChannelHistoryPresenter extends BasePresenter<ChannelHistoryView> {
    void setHistory(History history);

    List<VoiceMessage> getVoiceMessages();

}
