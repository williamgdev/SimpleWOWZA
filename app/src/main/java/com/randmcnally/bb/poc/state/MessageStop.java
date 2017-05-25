package com.randmcnally.bb.poc.state;

import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public class MessageStop implements MessageState {
    BBPlayer bbPlayer;

    public MessageStop(BBPlayer bbPlayer) {
        this.bbPlayer = bbPlayer;
    }

    @Override
    public void performAction(HistoryMessage historyMessage) {
//        historyMessage.setRemainingSeconds(0);
        bbPlayer.stop();
    }
}
