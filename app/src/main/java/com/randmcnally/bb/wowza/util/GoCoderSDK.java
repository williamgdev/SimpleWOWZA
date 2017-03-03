package com.randmcnally.bb.wowza.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WZStreamConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

public class GoCoderSDK {
    private static final String SDK_API_KEY = "GOSK-5943-0103-A15E-86A3-BA36";
    private static final int PORT_NUMBER = 1935;
    private static String hostAddress;// = "f3bcf3.entrypoint.cloud.wowza.com";
    private static String appName;// = "app-9bba";
//    private static final String HOST_ADDRESS = "7ba482.entrypoint.cloud.wowza.com";
//    private static final String APP_NAME = "app-4361";
//

    private static final String TAG = "GoCoderSDK ->";
    private static GoCoderSDK ourInstance = new GoCoderSDK();
    private String streamName;


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


    public static GoCoderSDK getInstance() {
        return ourInstance;
    }

    private GoCoderSDK() {
    }

    public boolean initializeGoCoderSDK(Context context, String streamName, String hostAddress, String appName, WowzaStatusCallback statusCallback) {
        this.streamName = streamName;
        this.hostAddress = hostAddress;
        this.appName = appName;
        this.statusCallback = statusCallback;
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
        goCoderBroadcastConfig.setHostAddress(hostAddress);
        goCoderBroadcastConfig.setPortNumber(PORT_NUMBER);
        goCoderBroadcastConfig.setApplicationName(appName);
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


    public static String _getUrlStream() {
        return "rtsp://f3bcf3.entrypoint.cloud.wowza.com:1935/app-9bba/0eea2fb3";
    }

    public boolean isBroadcasting() {
        WZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();
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

//    public void _startStreaming(){
//        WowzaConfig streamConfig = new WowzaConfig();
//        streamConfig.setHostAddress(HOST_ADDRESS);
//        streamConfig.setApplicationName(APP_NAME);
//        streamConfig.setStreamName(streamName);
//        goCoder.setConfig(streamConfig);
//        //Make sure streaming isn't already active
//        if (!this.goCoder.isStreaming()) {
//            // Validate the current broadcast config
//            WZStreamingError configValidationError = this.goCoder.getConfig().validateForBroadcast();
//            if (configValidationError != null) {
//                WZLog.error(configValidationError);
//            } else {
//                // Start the live stream
//                goCoder.startStreaming(new WZStatusCallback() {
//                    @Override
//                    public void onWZStatus(WZStatus wzStatus) {
//                        switch (wzStatus.getState()) {
//                            case WZState.IDLE:
//                                WZLog.info(TAG, "Streaming is not active");
//                                break;
//
//                            case WZState.STARTING:
//                                WZLog.info(TAG, "Broadcast initialization");
//                                break;
//
//                            case WZState.READY:
//                                WZLog.info(TAG, "Ready to begin streaming");
//                                break;
//
//                            case WZState.RUNNING:
//                                WZLog.info(TAG, "Streaming is active");
//                                break;
//
//                            case WZState.STOPPING:
//                                WZLog.info(TAG, "Broadcast shutdown");
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onWZError(final WZStatus wzStatus) {
//                        // Run this on the U/I thread
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(context, "Live Streaming Error: " + wzStatus.getLastError().getErrorDescription(), Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//
//                });
//            }
//        }
//    }

}
