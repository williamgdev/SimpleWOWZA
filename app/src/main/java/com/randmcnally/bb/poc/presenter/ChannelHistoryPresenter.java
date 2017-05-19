package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

import java.util.List;

public interface ChannelHistoryPresenter extends BasePresenter<ChannelHistoryView> {
    void setHistory(History history);

    void setOpenFireServer(OpenFireServer openFireServer);

    List<HistoryMessage> getHistoryMessages();

    void setChannelName(String channelName);

    void updateHistory();
}
