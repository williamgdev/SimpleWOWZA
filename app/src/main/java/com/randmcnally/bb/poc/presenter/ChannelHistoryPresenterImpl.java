package com.randmcnally.bb.poc.presenter;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

import java.util.List;

public class ChannelHistoryPresenterImpl implements ChannelHistoryPresenter {
    private ChannelHistoryView channelHistoryView;
    private History history;

    @Override
    public void attachView(ChannelHistoryView channelHistoryView) {
        this.channelHistoryView = channelHistoryView;
    }

    @Override
    public void detachView() {
        channelHistoryView = null;
    }

    @Override
    public void setHistory(History history) {
        this.history = history;
        if (getVoiceMessages().size() == 0) {
            channelHistoryView.showError("There is no messages to show");
        } else {
            channelHistoryView.updateHistory();
        }
    }

    @Override
    public List<VoiceMessage> getVoiceMessages() {
        return history.getVoiceMessages();
    }

}
