package com.randmcnally.bb.poc.state;

import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public class MessagePause implements MessageState {

    private final BBPlayer bbPlayer;
    private HistoryMessage historyMessage;

    public MessagePause(BBPlayer bbPlayer) {
        this.bbPlayer = bbPlayer;
    }

    @Override
    public void performAction(HistoryMessage historyMessage) {
        this.historyMessage = historyMessage;
        bbPlayer.pause();
    }
}
