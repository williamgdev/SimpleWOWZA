package com.randmcnally.bb.poc.util;

import com.red5pro.streaming.event.R5ConnectionListener;

public class ReceiverStream extends BaseStream{

    public ReceiverStream(R5ConnectionListener listener, String ipAddress) {
        super(listener, ipAddress);
    }

    public void play(String streamName) {
        stream.play(streamName);
    }

    public void stop() {
        stream.stop();
    }

}
