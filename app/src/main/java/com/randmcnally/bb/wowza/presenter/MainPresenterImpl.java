package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.view.MainView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;

import java.io.File;

public class MainPresenterImpl implements MainPresenter, StreamStatusCallback.ResultStreamStatusCallback{
    public static final String urlRSTP = "rtsp://f3bcf3.entrypoint.cloud.wowza.com:1935/app-9bba/0eea2fb3";
    private static final String TAG = "MainPresenterImpl";

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
    StreamStatusCallback streamStatusCallback;

    private MediaPlayer mediaPlayer;

    private File savedFile;

    ApiService apiService;

    Context context;

    MainView mainView;

    public MainPresenterImpl(Context context) {
        this.context = context;

        // Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(context, "GOSK-5943-0103-A15E-86A3-BA36");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(context,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = new WZAudioDevice();

        // Create a broadcaster instance
        goCoderBroadcaster = new WZBroadcast();

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = new WZBroadcastConfig();

        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Cloud account
        goCoderBroadcastConfig.setHostAddress("f3bcf3.entrypoint.cloud.wowza.com");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("app-9bba");
        goCoderBroadcastConfig.setStreamName("a1abd153");

        // Disable video
        goCoderBroadcastConfig.setVideoEnabled(false);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

        // Use the WZMP4Writer to save the audio file while broadcasting
        mp4Writer = new WZMP4Writer();
        goCoderBroadcastConfig.registerAudioSink(mp4Writer);
        goCoderBroadcastConfig.registerVideoSink(mp4Writer);
        goCoderBroadcastConfig.setVideoEnabled(false);

        apiService = ServiceFactory.createAPiService();

    }


    public int changeStatusBroadcast() {
        WZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();
        statusCallback = new WowzaStatusCallback();
        streamStatusCallback = new StreamStatusCallback(this);

        if (configValidationError != null) {
//            Toast.makeText(context, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
            return -1;
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running
            stopBroadcastandStream();
            return 0;
        } else {
            // Start streaming

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File outputFile = FileUtil.getOutputMediaFile(savedFile, "Wowza");
            if (outputFile != null)
                mp4Writer.setFilePath(outputFile.toString());
            else {
                Log.w(TAG, "changeStatusBroadcast: " + "Could not create or access the directory in which to store the MP");
                return -1;
            }

            apiService.startLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);

            return 1;
        }
    }

    private void stopBroadcastandStream() {
        goCoderBroadcaster.endBroadcast(statusCallback);
        apiService.stopLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
    }

    public String getUrlStream() {
        return urlRSTP;
    }

    public void stopListen() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void startListen() {
        mediaPlayer = MediaPlayer.create(context, Uri.parse(savedFile.toString()));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }

    @Override
    public void loadData() {

    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        mainView = null;
    }

    @Override
    public void streamStarted(int resultCallback) {
        switch (resultCallback){
            case StreamStatusCallback.ResultStreamStatusCallback.DONE:
                goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, statusCallback);

                break;

            case StreamStatusCallback.ResultStreamStatusCallback.ERROR:
                stopBroadcastandStream();
                mainView.showError("Error");
                break;
        }

    }
}
