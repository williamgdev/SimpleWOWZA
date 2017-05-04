package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.util.ChannelUtil;

import java.io.Serializable;

public class LiveStream implements Serializable {

    private String streamName;

    private int id;

    public LiveStream(String streamName, int id) {
        this.streamName = streamName;
        this.id = id;
    }

    public LiveStream(String streamName) {
        this.streamName = streamName;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublishStreamName() {
        return ChannelUtil.getPublishName(streamName, id);
    }

}
