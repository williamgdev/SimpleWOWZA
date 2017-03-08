package com.randmcnally.bb.wowza.presenter;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.activity.ChannelActivity;
import com.randmcnally.bb.wowza.callback.M3UAvailableCallback;
import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.TrascoderCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.randmcnally.bb.wowza.custom.BBPlayer;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.util.SimulateInteractor;
import com.randmcnally.bb.wowza.view.MainView;
import com.wowza.gocoder.sdk.api.status.WZState;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class BroadcastPresenterImpl implements MainPresenter,
        StreamStatusCallback.ListenerStreamStatusCallback,
        WowzaStatusCallback.ListenerWowzaStatus, BBPlayer.ListenerBBPlayer,
        TrascoderCallback.ListenerTranscoderCallback{
    private static final String TAG = "Broadcast ->";

    StreamStatusCallback streamStatusCallback;
    M3UAvailableCallback m3UAvailableCallback;
    WowzaStatusCallback wowzaStatusCallback;
    TrascoderCallback transcordeCallback;

    MainView mainView;
    Context context;
    GoCoderSDK goCoderSDK;
    SimulateInteractor _rtspInteractor;
    String streamName, codeStream, rtspUrl, m3u8Url, hostAddress, appName, message;
    Date bcStartTime, transcoderStartTime;
    private boolean isBroadcasting, isStreaming,
            _isAsyncTaskListening, isListeningStream, islisteningTRSPUrl, ischeckingStreaming, isReceivingM3u8Url,
            isSimulated;


    private File savedFile;
    private AudioManager audioManager;

    ApiService apiService;
    long responseTime;
    long callTime;
    private BBPlayer bbPlayer;
    private boolean isCheckingtranscoder;
    private boolean isStoppingBroadcast;


    public BroadcastPresenterImpl(Context context, String streamName, String codeStream, String rtspUrl, String m3u8_url, String hostAddress, String appName) {
        this.context = context;
        this.streamName = streamName;
        this.rtspUrl = rtspUrl;
        this.m3u8Url = m3u8_url;
        this.codeStream = codeStream;
        this.hostAddress = hostAddress;
        this.appName = appName;
        apiService = ServiceFactory.createAPiService();
        streamStatusCallback = new StreamStatusCallback(this);
//        m3UAvailableCallback = new M3UAvailableCallback(this);
        wowzaStatusCallback = new WowzaStatusCallback(this);
        transcordeCallback = new TrascoderCallback(this);

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        try {
            bbPlayer = new BBPlayer(context, rtspUrl, this);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void checkIfStreamIsReady() {
        ischeckingStreaming = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                apiService.getState(codeStream).enqueue(streamStatusCallback);
            }
        }, 1000);

    }

    @Override
    public void loadData() {
        checkIfStreamIsReady();
//        checkifTranscoderIsConnected();
        goCoderSDK = GoCoderSDK.getInstance();
        if (goCoderSDK.initializeGoCoderSDK(context, streamName, hostAddress, appName, wowzaStatusCallback))
            return;
    }


    private void checkifTranscoderIsConnected() {
        isCheckingtranscoder = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                apiService.getTranscorderStatus(codeStream).enqueue(transcordeCallback);
            }
        }, 1000);
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
        isBroadcasting = true;
        if (!isStreaming) {
            checkIfStreamIsReady();
        }

        updateView(ChannelActivity.UIState.BROADCASTING_PREPARING);

        if (!goCoderSDK.isBroadcasting()) {
            // Start Broadcast
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//            File outputFile = FileUtil.getOutputMediaFile(savedFile, "Wowza");
//            if (outputFile != null)
//                goCoderSDK.setMp4WriterPath(outputFile.toString());
//            else {
//                Log.w(TAG, "changeStatusBroadcast: " + "Could not create or access the directory in which to store the MP");
//                mainView.showError("Could not create or access the directory in which to store the MP");
//            }
            goCoderSDK.startBroadcast();
            return true;
        }

        return false;
    }

    public void stopBroadcast() {
        if (isBroadcasting){
            if (audioManager.isMicrophoneMute() == false) {
                audioManager.setMicrophoneMute(true);
            }
        }
        isBroadcasting = false;
        isStoppingBroadcast = true;

        // Stop the broadcast that is currently running
        updateView(ChannelActivity.UIState.BROADCASTING_STOPPING);
        goCoderSDK.stopBroadcast(getTimeDelay());
        checkifTranscoderIsConnected();
    }

    private long getTimeDelay() {
        if (bcStartTime == null)
            return 0;
        long timeDelay;
        if (transcoderStartTime == null)
            transcoderStartTime = new Date();
        timeDelay = transcoderStartTime.getTime() - bcStartTime.getTime();

        transcoderStartTime = null;
        bcStartTime = null;
        return timeDelay;
    }

    public void _startStream() {
        /**
         * Start the Stream in the Wowza Cloud
         */
        isStreaming = true;
        Log.d(TAG, "startStream: ");
//        apiService.startLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
    }

    public void stopStream() {
        /**
         * Stop the Stream in the Wowza Cloud
         */
        if (isBroadcasting)
            goCoderSDK.stopBroadcast(0); //Here check if you need to use the timeDelay
        isStreaming = false;
        ischeckingStreaming = false;
        isCheckingtranscoder = false;
        bbPlayer.forceStop();
//        apiService.stopLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
        isListeningStream = false;
        updateView(ChannelActivity.UIState.LOADING);
    }

    private void showMessage(String error) {
        showToast(error);
    }

    // Start and Stop Broadcast listening the Rest Call Start/Stop LiveStream
    @Override
    public void notifyStreamStatus(int resultCallback) {
        switch (resultCallback) {
            case StreamStatusCallback.ListenerStreamStatusCallback.DONE:
                if (isBroadcasting)
                    updateView(ChannelActivity.UIState.BROADCASTING);
                else {
                    if (ischeckingStreaming) {
                        isStreaming = true;
                        ischeckingStreaming = false;
                        if (!isCheckingtranscoder)
                            checkifTranscoderIsConnected();
                        if (!bbPlayer.isPlaying()) {
                            updateView(ChannelActivity.UIState.READY);
                        }

                    } else if (isListeningStream) {
                        isListeningStream = false;
                        if (!isCheckingtranscoder)
                            checkifTranscoderIsConnected();
                        if (mainView != null) {
                            updateView(ChannelActivity.UIState.READY);
                        }
                    }
                }

                break;

            case StreamStatusCallback.ListenerStreamStatusCallback.ERROR:
//                stopStream();
                mainView.showError(streamStatusCallback.message);
                //It should show Error in the future
                updateView(ChannelActivity.UIState.CONFlICT);
                break;

            case StreamStatusCallback.ListenerStreamStatusCallback.WAITING:
                Log.d(TAG, "notifyStreamStatus: LOADING");
                checkIfStreamIsReady();
                if (mainView != null)
                    updateView(ChannelActivity.UIState.LOADING);
                break;

            case StreamStatusCallback.ListenerStreamStatusCallback.STOP:
                if (mainView != null) {
                    message = context.getString(R.string.stream_not_started);
                    updateView(ChannelActivity.UIState.ERROR);
                }
                if (isBroadcasting() || islisteningTRSPUrl || islisteningTRSPUrl) {
                    stopBroadcast();
                    islisteningTRSPUrl = false;

                }

                break;
        }
    }

    public boolean isBroadcasting() {
        return isBroadcasting;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPlaying() {
        return bbPlayer.isPlaying();
    }

    @Override
    public void notifyWowzaStatus(int state, final String message) {
        if (state == WZState.IDLE) {
            updateView(ChannelActivity.UIState.READY);
            isBroadcasting = false;
            if (isStoppingBroadcast){
                isStoppingBroadcast = false;
                audioManager.setMicrophoneMute(false);
            }
            showToast(message);
        }
        else if (state == WZState.READY){
            if (!isBroadcasting)
                stopBroadcast();
        }
        else if(state == WZState.RUNNING){
            if (isBroadcasting) {
                bcStartTime = new Date();
                MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
                mp.start();
                updateView(ChannelActivity.UIState.BROADCASTING);
                checkifTranscoderIsConnected();
            }
            else{
                stopBroadcast();
            }
        }
        else if (mainView != null && !((Activity) context).isFinishing()) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                mainView.showMessage(message);
                }
            });
        }
    }

    void updateView(final ChannelActivity.UIState state) {
        if (mainView != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainView.updateView(state);
                }
            });
        }
    }

    private void showToast(final String message){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message , Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void notifyTranscoderStatus(boolean isConnected, String message) {
        if (isStreaming && !isBroadcasting && !isStoppingBroadcast) {
            if (isCheckingtranscoder) {
                if (isConnected) {
                    if (bbPlayer.isPlaying())
                        checkifTranscoderIsConnected();
                    else {
                        isCheckingtranscoder = false;
                        startPlayRTSP();
                        showToast(message);
                    }
                } else {
                    if (bbPlayer.isPlaying()){
                        if (ischeckingStreaming) {
                            checkifTranscoderIsConnected();
                        }
                        else{
//                            bbPlayer.stop();
                            Log.d(TAG, "notifyTranscoderStatus: Broadcast Stop and the BBPlayer is muted");

                            bbPlayer.mute();

//                            if (isCheckingtranscoder)
//                                isCheckingtranscoder = false;
                        }
                    }
                    else
                        checkifTranscoderIsConnected();
                }
            }
            else {
                showToast("notifyTranscoderStatus: Case need to catch");
            }
        }
        else if(isStreaming && isBroadcasting){
            if(isConnected) {
                transcoderStartTime = new Date();
                isCheckingtranscoder = false;
            }
            else{
                checkifTranscoderIsConnected();
            }
        }

    }
//
//    private void stopPlayRTSP() {
//        bbPlayer.stop();
////        ischeckingStreaming = true;
//        checkifTranscoderIsConnected();
//
//    }

    private void startPlayRTSP() {
        bbPlayer.start();

    }

    @Override
    public void onListener(BBPlayer.BBPLAYER state) {
        switch (state) {
            case PLAYING:
                updateView(ChannelActivity.UIState.RECEIVING);
                if (!isCheckingtranscoder)
                    checkifTranscoderIsConnected();
                break;

            case STOPPED:
                updateView(ChannelActivity.UIState.READY);
                checkifTranscoderIsConnected();
                break;
            case ERROR_UNKNOWN:
                updateView(ChannelActivity.UIState.ERROR);
                message = "Error!";
                break;

            case AUDIO_STREAM_COMPLETED:
                showToast(state.toString());
                updateView(ChannelActivity.UIState.READY);
                checkifTranscoderIsConnected();
                break;

//            case AUDIO_STREAM_END:
//                Toast.makeText(context, "CREATE A STATE AUDIO_STREAM_END", Toast.LENGTH_SHORT).show();
//                break;
//            case AUDIO_STREAM_START:
//                Toast.makeText(context, "CREATE A STATE AUDIO_STREAM_START", Toast.LENGTH_SHORT).show();
//                break;
//            case INFO_UNKNOWN:
//                Toast.makeText(context, "CREATE A STATE INFO_UNKNOWN", Toast.LENGTH_SHORT).show();
//                break;
//            case PREPARING:
//                Toast.makeText(context, "CREATE A STATE PREPARING", Toast.LENGTH_SHORT).show();
//                break;
        }
    }
}
