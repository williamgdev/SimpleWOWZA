package com.randmcnally.bb.poc.presenter;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

public interface ChannelHistoryPresenter extends BasePresenter<ChannelHistoryView> {
    void setHistory(History history);

    void onMessagePause(int position, SeekBar seekBar);

    void onMessagePlay(int position, SeekBar seekBar);

    String getMessageName(int position);

    int getMessageSize();
}
