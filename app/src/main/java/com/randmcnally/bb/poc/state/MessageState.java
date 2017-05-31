package com.randmcnally.bb.poc.state;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public interface MessageState{
    void performAction(HistoryMessage historyMessage, SeekBar seekBar);
    void updateUI(HistoryAdapter.HistoryViewHolder viewHolder);

}