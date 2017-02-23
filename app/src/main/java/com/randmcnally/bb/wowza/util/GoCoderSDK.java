package com.randmcnally.bb.wowza.util;

import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;

public class GoCoderSDK {
    private static final String HOST_ADDRESS = "f3bcf3.entrypoint.cloud.wowza.com";
    private static final String SDK_API_KEY = "GOSK-5943-0103-A15E-86A3-BA36";
    private static final int PORT_NUMBER = 1935;
    private static final String APP_NAME = "app-9bba";
    private static GoCoderSDK ourInstance = new GoCoderSDK();


    // The top level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK audio device
    private WZAudioDevice goCoderAudioDevice;

    // The GoCoder SDK broadcaster
    private WZBroadcast goCoderBroadcaster;

    // The broadcast configuration settings
    private WZBroadcastConfig goCoderBroadcastConfig;
    private WZMP4Writer mp4Writer;

    WowzaStatusCallback statusCallback;
    Context context;


    public static GoCoderSDK getInstance() {
        return ourInstance;
    }

    private GoCoderSDK() {
    }

    public boolean initializeGoCoderSDK(Context context, String streamName) {
        this.context = context;
        // Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(context, SDK_API_KEY);

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(context,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return true;
        }

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = new WZAudioDevice();

        // Create a broadcaster instance
        goCoderBroadcaster = new WZBroadcast();

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = new WZBroadcastConfig();

        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Cloud account
        goCoderBroadcastConfig.setHostAddress(HOST_ADDRESS);
        goCoderBroadcastConfig.setPortNumber(PORT_NUMBER);
        goCoderBroadcastConfig.setApplicationName(APP_NAME);
        goCoderBroadcastConfig.setStreamName(streamName);

        // Disable video
        goCoderBroadcastConfig.setVideoEnabled(false);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

        // Use the WZMP4Writer to save the audio file while broadcasting
        mp4Writer = new WZMP4Writer();
        goCoderBroadcastConfig.registerAudioSink(mp4Writer);
        goCoderBroadcastConfig.registerVideoSink(mp4Writer);
        goCoderBroadcastConfig.setVideoEnabled(false);

        return true;
    }


    public static String getUrlStream() {
        return "rtsp://f3bcf3.entrypoint.cloud.wowza.com:1935/app-9bba/0eea2fb3";
    }

    public boolean isBroadcasting() {
        WZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();
        statusCallback = new WowzaStatusCallback(context);
        if (configValidationError != null) {
//            Toast.makeText(context, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
            return false;
        }
        return goCoderBroadcaster.getStatus().isRunning();
    }

    public WZMP4Writer getMp4Writer() {
        return mp4Writer;
    }

    public void setMp4WriterPath(String mp4WriterPath) {
        this.mp4Writer.setFilePath(mp4WriterPath);
    }

    public void startBroadcast() {
        if (!isBroadcasting())
            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, statusCallback);
    }

    public void stopBroadcast() {
        if (isBroadcasting())
            goCoderBroadcaster.endBroadcast(statusCallback);
    }
}
