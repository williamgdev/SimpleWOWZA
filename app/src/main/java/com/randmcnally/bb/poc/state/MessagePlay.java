package com.randmcnally.bb.poc.state;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public class MessagePlay implements MessageState{
    private final HistoryAdapter historyAdapter;
    private HistoryMessage historyMessage;

    public MessagePlay(HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
    }

    @Override
    public void performAction(HistoryMessage historyMessage,SeekBar seekBar) {
        this.historyMessage = historyMessage;
        historyAdapter.playHistoryMessage(historyMessage, seekBar);

    }

    @Override
    public void updateUI(HistoryAdapter.HistoryViewHolder viewHolder) {
        viewHolder.play();
    }


}