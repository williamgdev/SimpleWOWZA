package com.randmcnally.bb.wowza.presenter;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.util.GoCoderSDK;
import com.randmcnally.bb.wowza.util.RTSPInteractor;
import com.randmcnally.bb.wowza.view.MainView;

import java.io.File;
import java.io.IOException;

public class BroadcastPresenterImpl implements MainPresenter,
        StreamStatusCallback.ListenerStreamStatusCallback, MediaPlayer.OnErrorListener,
        WowzaStatusCallback.ListenerWowzaStatus,
        TrascoderCallback.ListenerTranscoderCallback{
    private static final String TAG = "Broadcast ->";

    StreamStatusCallback streamStatusCallback;
    M3UAvailableCallback m3UAvailableCallback;
    WowzaStatusCallback wowzaStatusCallback;
    TrascoderCallback transcordeCallback;

    MainView mainView;
    Context context;
    GoCoderSDK goCoderSDK;
    RTSPInteractor _rtspInteractor;
    String streamName, codeStream, rtspUrl, m3u8Url, hostAddress, appName, message;
    private boolean isBroadcasting, isStreaming,
            _isAsyncTaskListening, isListeningStream, islisteningTRSPUrl, ischeckingStreaming, isReceivingM3u8Url,
            isSimulated;


    private File savedFile;

    ApiService apiService;
    long responseTime;
    long callTime;
    private MediaPlayer mediaPlayer;
    private boolean isCheckingtranscoder;


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
        mediaPlayer = new MediaPlayer();


    }

    private void checkIfStreamIsReady() {
//        if(isListeningStream)
        ischeckingStreaming = true;
//        if (isSimulated) {
//            if (isBroadcasting)
//                updateView(ChannelActivity.UIState.BROADCASTING);
//            else {
//                if (ischeckingStreaming) {
//                    isStreaming = true;
//                    ischeckingStreaming = false;
//                    islisteningTRSPUrl = true;
//                    updateView(ChannelActivity.UIState.READY);
//                } else if (isListeningStream) {
//                    isListeningStream = false;
//                    updateView(ChannelActivity.UIState.READY);
//                }
//                checkIfM3u8isAvailable();
//            }
//            updateView(ChannelActivity.UIState.READY);
//
//            return;
//        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                apiService.getState(codeStream).enqueue(streamStatusCallback);
            }
        }, 1000);

    }

    @Override
    public void loadData() {

//      comment isSimulated when you are ready to work.
//        isSimulated = true;
//        islisteningTRSPUrl = true;
//        isListeningStream = true;

//        rtspInteractor = RTSPInteractor.getInstance();

        if (isSimulated)
            simulate();
        else {
            checkIfStreamIsReady();
//            checkIfM3u8isAvailable();
            checkifTranscoderIsConnected();
            goCoderSDK = GoCoderSDK.getInstance();
            if (goCoderSDK.initializeGoCoderSDK(context, streamName, hostAddress, appName, wowzaStatusCallback))
                return;
        }
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

    private void simulate() {
        updateView(ChannelActivity.UIState.LOADING);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (mainView != null) {
                                updateView(ChannelActivity.UIState.READY);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        m3u8Url = "http://7ba482.entrypoint.cloud.wowza.com/app-4361/ngrp:0074c461_all/playlist.m3u8";
        rtspUrl = "rtsp://7ba482.entrypoint.cloud.wowza.com/app-4361";
        islisteningTRSPUrl = true;

        checkIfStreamIsReady();
//        checkIfMusicIsPlaying();
        goCoderSDK = GoCoderSDK.getInstance();
        if (goCoderSDK.initializeGoCoderSDK(context, "098d53ab", "7ba482.entrypoint.cloud.wowza.com", "app-4361", wowzaStatusCallback))
            return;

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
        islisteningTRSPUrl = false;
        if (!isStreaming) {
            checkIfStreamIsReady();
        }
//        speakAsyncTask.cancel(true);
        updateView(ChannelActivity.UIState.BROADCASTING);

        if (!goCoderSDK.isBroadcasting()) {
            // Start Broadcast
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

    public void stopBroadcast() {
//        islisteningTRSPUrl = true;
        isBroadcasting = false;

        // Stop the broadcast that is currently running
        goCoderSDK.stopBroadcast();
        updateView(ChannelActivity.UIState.READY);
//        rtspInteractor.stopPlay();
//        checkIfM3u8isAvailable();
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
        if (isBroadcasting())
            stopBroadcast();
//        apiService.stopLiveStream(ServiceFactory.STREAM_ID).enqueue(streamStatusCallback);
        isListeningStream = false;
    }

    public void _stopListen() {
//        if (rtspInteractor.isPlaying()) {
//            mediaPlayer.stop();
//            rtspInteractor.stopPlay();
////            mediaPlayer = new MediaPlayer();
//            islisteningTRSPUrl = false;
//            checkIfM3u8isAvailable();
//
//            showToast("Stop the audio Stream");
//
//        }
            updateView(ChannelActivity.UIState.READY);


        checkIfStreamIsReady();
    }

    public void _startListen() {
        Log.d(TAG, "startListen: ");
        if (!islisteningTRSPUrl) {
            _stopListen();
            return;
        }
        if (isBroadcasting) {
            Log.d(TAG, "startListen: CAll RETURN");
            return;
        }
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();

                updateView(ChannelActivity.UIState.RECEIVING);

                islisteningTRSPUrl = true;
//                rtspInteractor.startPlay();
//                checkIfM3u8isAvailable();
            }
        });

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(rtspUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();

        showToast("Receiving the audio Stream");



        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showMessage("Complete");
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                showMessage("Info");

                return false;

            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        showMessage("Server Die");
                        break;

                    default:
                        Log.d(TAG, "onError: Still the audio is available");
                        break;
                }
                if (islisteningTRSPUrl)
                    _stopListen();
                return true;
            }
        });
    }

    private void showMessage(String error) {
        showToast(error);
    }

    private void _checkIfM3u8isAvailable() {
//        rtspInteractor.checkRTSPUrl(m3UAvailableCallback, m3u8Url);


    }

    // Start and Stop Broadcast listening the Rest Call Start/Stop LiveStream
    @Override
    public void notifyStreamStatus(int resultCallback) {
        switch (resultCallback) {
            case StreamStatusCallback.ListenerStreamStatusCallback.DONE:
//                startBroadcast();
//                mainView.showMessage(streamStatusCallback.message);
                if (isBroadcasting)
                    updateView(ChannelActivity.UIState.BROADCASTING);
                else {
                    if (ischeckingStreaming) {
                        isStreaming = true;
                        ischeckingStreaming = false;
                        if (!isCheckingtranscoder)
                            checkifTranscoderIsConnected();
                        if (!mediaPlayer.isPlaying()) {
                            updateView(ChannelActivity.UIState.READY);
                        }

                    } else if (isListeningStream) {
                        isListeningStream = false;
                        if (mainView != null) {
                            updateView(ChannelActivity.UIState.READY);
                        }
                    }
//                    checkIfM3u8isAvailable();
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

    public void _checkIfRTSPIsReady() {
        if (!isBroadcasting) {
            speakAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    _startListen();
                }
            });

        }

    }

    public boolean isStreaming() {
        return isStreaming;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: ");
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

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

//    @Override
//    public void _notifyM3UStatus(M3UStatus status) {
//        switch (status) {
//            case AVAILABLE:
//                isReceivingM3u8Url = true;
//                if (!mediaPlayer.isPlaying()) {
//                    _startListen();
//
//                }
//                break;
//
//            case ERROR:
//                isReceivingM3u8Url = false;
//                if (!mediaPlayer.isPlaying()) {
////                    checkIfM3u8isAvailable();
//                }
//                break;
//        }
//    }

    @Override
    public void notifyWowzaStatus(int state, final String message) {

        showToast(message);

    }

    public String getMessage() {
        return message;
    }

    private void updateView(final ChannelActivity.UIState state) {
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
        if (isStreaming) {
            if (isCheckingtranscoder) {
                if (isConnected) {
                    isCheckingtranscoder = false;
                    startPlayRTSP();
                    showToast(message);
                } else {
                    checkifTranscoderIsConnected();
                }
            }
            else {
                showToast("notifyTranscoderStatus: Case need to catch");
            }
        }

    }

    private void stopPlayRTSP() {
        mediaPlayer.stop();
//        ischeckingStreaming = true;
        checkifTranscoderIsConnected();

    }

    private void startPlayRTSP() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                updateView(ChannelActivity.UIState.RECEIVING);
            }
        });

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(rtspUrl);
        } catch (IOException e) {
            e.printStackTrace();
            showToast("startPlayRTSP: Case need to catch");
        }
        mediaPlayer.prepareAsync();
        showToast("Receiving the audio Stream");

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showMessage("Complete");
                if (!isCheckingtranscoder)
                    checkifTranscoderIsConnected();
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        showMessage("MEDIA_INFO_BUFFERING_END");
                        break;

                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        showMessage("MEDIA_INFO_BUFFERING_START");
                        break;

                    default:
                        showMessage("setOnInfoListener: Case need to catch");

                }

                return false;

            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        showMessage("Server Die");
                        break;

                    default:
                        Log.d(TAG, "onError: Still the audio is available");
                        break;
                }
                stopPlayRTSP();
                return true;
            }
        });

    }
}
