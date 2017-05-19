package com.randmcnally.bb.poc.state;

import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

public interface MessageState{
    void performAction(HistoryMessage historyMessage);


}