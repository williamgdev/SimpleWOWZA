package com.randmcnally.bb.poc.util;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionListener;

public class BaseStream {
    static final String IP_ADDRESS = "192.168.43.212";
    //  private static final String IP_ADDRESS = "192.168.1.233";
    static final int STREAM_PORT = 8554;
    static final int API_PORT = 5080;
    static final String SDK_LICENSE_KEY = "2WDZ-GOA3-XZJJ-YFZE";
    static final String APP_ID = "com.randmcnally.bb.poc";

    public static final String ACCESS_TOKEN = "123";
    public static final String APP_NAME = "live";


    R5Configuration configuration;
    R5Stream stream;
    R5Connection connection;

    public BaseStream(R5ConnectionListener listener) {
        configuration = new R5Configuration(R5StreamProtocol.RTSP, IP_ADDRESS,  STREAM_PORT, APP_NAME, 1.0f);
        configuration.setLicenseKey(SDK_LICENSE_KEY);
        configuration.setBundleID(APP_ID);
        connection = new R5Connection(configuration);
        stream = new R5Stream(connection);
        stream.setListener(listener);
    }

    public static String getURLStream(String streamName){
        return "http://" + IP_ADDRESS + ":" + STREAM_PORT + "/api/v1/applications/live/streams/" + streamName + "?accessToken=" + ACCESS_TOKEN;
    }

    public static String getBaseUrlAPI(){
        return "http://" + IP_ADDRESS + ":" + API_PORT + "/api/v1/";
    }
}

