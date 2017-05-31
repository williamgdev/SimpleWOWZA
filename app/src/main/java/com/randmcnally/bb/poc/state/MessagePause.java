package com.randmcnally.bb.poc.state;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public class MessagePause implements MessageState {

    private final HistoryAdapter historyAdapter;
    private HistoryMessage historyMessage;

    public MessagePause(HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
    }

    @Override
    public void performAction(HistoryMessage historyMessage, SeekBar seekBar) {
        this.historyMessage = historyMessage;
        historyAdapter.pauseMessage(historyMessage);
    }

    @Override
    public void updateUI(HistoryAdapter.HistoryViewHolder viewHolder) {
        viewHolder.pause();
    }
}
