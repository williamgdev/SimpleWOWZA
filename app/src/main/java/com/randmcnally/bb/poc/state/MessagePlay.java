package com.randmcnally.bb.poc.state;

import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.util.FileUtil;

import java.io.IOException;

public class MessagePlay implements MessageState{
    private HistoryMessage historyMessage;
    private BBPlayer bbPlayer;

    public MessagePlay(HistoryAdapter historyAdapter) {
        this.bbPlayer = historyAdapter.getBBPlayer();
//        historyMessage.setDuration(FileUtil.getMetaDataFileDuration(historyAdapter.getContext(), "http://192.168.43.212:5080/live/streams/randmcnally_1.flv"));
    }

    @Override
    public void performAction(HistoryMessage historyMessage) {
        this.historyMessage = historyMessage;
        try {
            bbPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}