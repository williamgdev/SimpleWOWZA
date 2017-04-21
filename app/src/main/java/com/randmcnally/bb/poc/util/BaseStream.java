package com.randmcnally.bb.poc.util;

import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
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
        configuration = new R5Configuration(R5StreamProtocol.RTSP, Red5ProApiInteractor.IP_ADDRESS,  Red5ProApiInteractor.STREAM_PORT, Red5ProApiInteractor.APP_NAME, 1.0f);
        configuration.setLicenseKey(Red5ProApiInteractor.SDK_LICENSE_KEY);
        configuration.setBundleID(Red5ProApiInteractor.APP_ID);
        connection = new R5Connection(configuration);
        stream = new R5Stream(connection);
        stream.setListener(listener);
    }

}

