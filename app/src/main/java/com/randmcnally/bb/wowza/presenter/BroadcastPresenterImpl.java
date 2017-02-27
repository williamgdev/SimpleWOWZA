package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.activity.ChannelActivity;

import java.io.File;
import java.io.IOException;

public class BroadcastPresenterImpl implements MainPresenter, StreamStatusCallback.ListenerStreamStatusCallback, MediaPlayer.OnErrorListener {
    private static final String TAG = "Broadcast ->";

    StreamStatusCallback streamStatusCallback;

    ChannelActivity mainView;
    Context context;
    GoCoderSDK goCoderSDK;
    String streamName;
    private boolean isBroadcasting, isStreaming, isAsyncTaskListening;

    private File savedFile;

    ApiService apiService;
    long responseTime;
    long callTime;
    private MediaPlayer mediaPlayer;


    public BroadcastPresenterImpl(Context context, String streamName) {
        this.context = context;
        this.streamName = streamName;
        apiService = ServiceFactory.createAPiService();
        streamStatusCallback = new StreamStatusCallback(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(this);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mainView.updateUI(ChannelActivity.UIState.RECEIVING);
                isAsyncTaskListening = false;
            }
        });
    }

    private void checkIfStreamIsReady() {
        apiService.getState(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
//        if (responseTime - callTime > 500){
//            apiService.getState(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
//            Log.d(TAG, "checkIfStreamIsReady: CallTime");
//        }
//        else {
//            countDownTimer.start();
//        }
    }
//
//    CountDownTimer countDownTimer = new CountDownTimer(500, 100) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//        }
//
//        @Override
//        public void onFinish() {
//            callTime = System.currentTimeMillis();
//            Log.d(TAG, "onFinish: CallTime");
//            apiService.getState(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
//        }
//    };


    @Override
    public void loadData() {
        checkIfStreamIsReady();
        goCoderSDK = GoCoderSDK.getInstance();
        if (goCoderSDK.initializeGoCoderSDK(context, streamName)) return;
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = (ChannelActivity) mainView;
    }

    @Override
    public void detachView() {
        mainView = null;

    }

    public boolean startBroadcast() {
        isBroadcasting = true;
//        speakAsyncTask.cancel(true);
        mainView.updateUI(ChannelActivity.UIState.BROADCASTING);
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
            goCoderSDK.startBroadcast();
            return true;
        }

        return false;
    }

    public void stopBroadcast(){
        // Stop the broadcast that is currently running
        goCoderSDK.stopBroadcast();
        isBroadcasting = false;
        mainView.updateUI(ChannelActivity.UIState.READY);
        checkIfRTSPIsReady();
    }

    public void startStream(){
        /**
         * Start the Stream in the Wowza Cloud
         */
        isStreaming = true;
        apiService.startLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
    }

    public void stopStream(){
        /**
         * Stop the Stream in the Wowza Cloud
         */
//        stopBroadcast();
//        apiService.stopLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
    }

    public void stopListen() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void startListen(){
        if (!isAsyncTaskListening) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mainView.updateUI(ChannelActivity.UIState.READY);
                }
            });
            isAsyncTaskListening = true;
        }
        if (isBroadcasting){
            Log.d(TAG, "startListen: CAll RETURN");
            return;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(GoCoderSDK.getUrlStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            startListen();
        }
    }

    // Start and Stop Broadcast listening the Rest Call Start/Stop LiveStream
    @Override
    public void listenerStreamStatus(int resultCallback) {
        switch (resultCallback) {
            case StreamStatusCallback.ListenerStreamStatusCallback.DONE:
//                startBroadcast();
//                mainView.showMessage(streamStatusCallback.message);
                if(isBroadcasting)
                    mainView.updateUI(ChannelActivity.UIState.BROADCASTING);
                else {
                    if (isStreaming)
                        mainView.updateUI(ChannelActivity.UIState.READY);
                    else
                        checkIfRTSPIsReady();
                    break;
                }

            case StreamStatusCallback.ListenerStreamStatusCallback.ERROR:
                stopStream();
//                mainView.hideProgress();
                mainView.showError(streamStatusCallback.message);
                //It should show Error in the future
                mainView.updateUI(ChannelActivity.UIState.CONFlICT);
                break;

            case StreamStatusCallback.ListenerStreamStatusCallback.WAITING:
//                responseTime = System.currentTimeMillis();
                checkIfStreamIsReady();
                mainView.updateUI(ChannelActivity.UIState.LOADING);
                break;

            case StreamStatusCallback.ListenerStreamStatusCallback.STOP:
                mainView.updateUI(ChannelActivity.UIState.LOADING);
                startStream();
                break;
        }
    }

    public void checkIfRTSPIsReady() {
        if (!isBroadcasting){
            speakAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    startListen();
                }
            });

        }

    }

    public boolean isStreaming() {
        return isStreaming;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " );
        return false;
    }

    public boolean isBroadcasting() {
        return isBroadcasting;
    }

    public AsyncTask speakAsyncTask = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    };


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
