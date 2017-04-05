package com.randmcnally.bb.poc.util;

import com.randmcnally.bb.poc.network.Red5ProApiManager;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionListener;

public class BaseStream {


    R5Configuration configuration;
    R5Stream stream;
    R5Connection connection;

    public BaseStream(R5ConnectionListener listener) {
        configuration = new R5Configuration(R5StreamProtocol.RTSP, Red5ProApiManager.IP_ADDRESS,  Red5ProApiManager.STREAM_PORT, Red5ProApiManager.APP_NAME, 1.0f);
        configuration.setLicenseKey(Red5ProApiManager.SDK_LICENSE_KEY);
        configuration.setBundleID(Red5ProApiManager.APP_ID);
        connection = new R5Connection(configuration);
        stream = new R5Stream(connection);
        stream.setListener(listener);
    }

}

