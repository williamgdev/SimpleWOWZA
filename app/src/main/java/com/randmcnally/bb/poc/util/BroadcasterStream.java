package com.randmcnally.bb.poc.util;

import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Microphone;

public class BroadcasterStream extends BaseStream{

    public BroadcasterStream(R5ConnectionListener listener) {
        super(listener);
        R5Microphone r5Microphone = new R5Microphone();
        stream.attachMic(r5Microphone);
    }

    public void startBroadcast(String streamName) {
        stream.publish(streamName, R5Stream.RecordType.Record);
    }

    public void stopBroadcast() {
        stream.stop();
    }

}
