package com.randmcnally.bb.poc.presenter;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

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
        if (history.getVoiceMessages().size() == 0) {
            channelHistoryView.showError("There is no messages to show");
        } else {
            channelHistoryView.updateHistory();
        }
    }

    @Override
    public void onMessagePause(int position, SeekBar seekBar) {

    }

    @Override
    public void onMessagePlay(int position, SeekBar seekBar) {

    }

    @Override
    public String getMessageName(int position) {
        return history.getVoiceMessages().get(position).getName();
    }

    @Override
    public int getMessageSize() {
        return history.getVoiceMessages().size();
    }

}
