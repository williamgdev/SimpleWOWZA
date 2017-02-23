package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.view.MainView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;

import java.io.File;

public class BroadcastPresenterImpl implements MainPresenter, StreamStatusCallback.ResultStreamStatusCallback {
    private static final String TAG = "Broadcast ->";

    StreamStatusCallback streamStatusCallback;

    MainView mainView;
    Context context;
    GoCoderSDK goCoderSDK;
    String streamName;

    private File savedFile;

    ApiService apiService;


    public BroadcastPresenterImpl(Context context, String streamName) {
        this.context = context;
        this.streamName = streamName;
        loadData();
    }

    @Override
    public void loadData() {
        goCoderSDK = GoCoderSDK.getInstance();
        if (goCoderSDK.initializeGoCoderSDK(context, streamName)) return;
        apiService = ServiceFactory.createAPiService();
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        mainView = null;

    }

    public boolean startBroadcast() {
        if (!goCoderSDK.isBroadcasting()){
            // Start streaming
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File outputFile = FileUtil.getOutputMediaFile(savedFile, "Wowza");
            if (outputFile != null)
                goCoderSDK.setMp4WriterPath(outputFile.toString());
            else {
                Log.w(TAG, "changeStatusBroadcast: " + "Could not create or access the directory in which to store the MP");
                mainView.showError("Could not create or access the directory in which to store the MP");
            }
//            apiService.startLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
            goCoderSDK.startBroadcast();
            return true;
        }

        return false;
    }

    public void stopBroadcast(){
        // Stop the broadcast that is currently running
        goCoderSDK.stopBroadcast();
//        apiService.stopLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);

    }

    // Start and Stop Broadcast listening the Rest Call Start/Stop LiveStream
    @Override
    public void listenerStreamStatus(int resultCallback) {
        switch (resultCallback) {
            case StreamStatusCallback.ResultStreamStatusCallback.DONE:
                startBroadcast();
                mainView.showMessage(streamStatusCallback.message);
                break;

            case StreamStatusCallback.ResultStreamStatusCallback.ERROR:
                stopBroadcast();
                mainView.showError(streamStatusCallback.message);
                break;
        }
    }




//    public int changeStatusBroadcast() {
//        streamStatusCallback = new StreamStatusCallback(this);
//
//
//        } else if (goCoderBroadcaster.getStatus().isRunning()) {
//            // Stop the broadcast that is currently running
//            stopBroadcastandStream();
//            return 0;
//        } else {
//            // Start streaming
//
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//            File outputFile = FileUtil.getOutputMediaFile(savedFile, "Wowza");
//            if (outputFile != null)
//                mp4Writer.setFilePath(outputFile.toString());
//            else {
//                Log.w(TAG, "changeStatusBroadcast: " + "Could not create or access the directory in which to store the MP");
//                return -1;
//            }
//
//            apiService.startLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
//
//            return 1;
//        }
//    }




}
