package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.restservice.OpenFireApiService;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.MainView;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Date;


public class BroadcastPresenterImpl implements MainPresenter,
        R5ConnectionListener, OpenFireServer.OpenFireServerListener{
    private static final String TAG = "Broadcast ->";

    private final AudioManager audioManager;

    MainView mainView;
    Context context;
    ChannelInteractor interactor;
    String message;

    private boolean isStreaming;
    Date streamStartTime;
    Date bcStartTime;
    private String receiverStreamName;


    OpenFireApiService apiService;
    OpenFireServer openFireServer;


    public BroadcastPresenterImpl(Context context, String streamName, String channelName) {
        this.context = context;
        this.interactor = new ChannelInteractor(streamName, channelName, this);

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        String uniqueUID = FileUtil.getDeviceUID(context);
        openFireServer = OpenFireServer.getInstance(uniqueUID);
        openFireServer.setListener(this);

    }


        public boolean isPreparing() {
        return preparing;
    }

    @Override
    public void loadData() {
        updateView(ChannelActivity.UIState.READY);
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        mainView = null;
        interactor.stopListen();

    }

    static boolean preparing;
    public void startBroadcast() {
        bcStartTime = new Date();

        preparing = true;
        if (audioManager.isMicrophoneMute() == false) {
            audioManager.setMicrophoneMute(true);
        }
        updateView(ChannelActivity.UIState.BROADCASTING_PREPARING);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preparing) {
                    preparing = false;
                    final MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
                    mp.start();
                    audioManager.setMicrophoneMute(false);

                    interactor.startBroadcast(); //Send the filename to the Receiver

                    updateView(ChannelActivity.UIState.BROADCASTING);
                }
            }
        }, 500);
    }

    public void stopBroadcast() {
        if (preparing){
            preparing = false;
            updateView(ChannelActivity.UIState.READY);
            return;
        }
        updateView(ChannelActivity.UIState.BROADCASTING_STOPPING);
        long time = getTimeDelay();

        audioManager.setMicrophoneMute(true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                interactor.stop();
                audioManager.setMicrophoneMute(false);
                updateView(ChannelActivity.UIState.READY);
//                sendNotification(false);
            }
        }, time);

    }


    public boolean isBroadcasting() {
        return interactor.isBroadcasting();
    }

    public String getMessage() {
        return message;
    }

    public boolean isPlaying() {
        return interactor.isListening();
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

    public void startListen(String streamName, String stream_id) {
        bcStartTime = new Date();
        this.receiverStreamName = streamName;
        if (interactor.isBroadcasting()){
            interactor.stop();
        }
        if(interactor.isMute() || !isPlaying()){
            isStreaming = true;
            interactor.play(receiverStreamName, stream_id);
            updateView(ChannelActivity.UIState.RECEIVING);
        }
    }


    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
//        showToast(r5ConnectionEvent.name());
        switch (r5ConnectionEvent) {
            case DISCONNECTED:
                if (isStreaming) {
                    updateView(ChannelActivity.UIState.READY);
                    isStreaming = false;
                }
                break;
            case START_STREAMING:
                if (isBroadcasting()) {
                    openFireServer.sendNotification(interactor.getStreamName(), interactor.getCounter());
                    showToast("STREAMING");
                }
                streamStartTime = new Date();

                break;
            case NET_STATUS:
                switch (r5ConnectionEvent.message){
                    case "NetStream.Play.UnpublishNotify":
                        interactor.muteAudio();
                        interactor.stopListen();
                        updateView(ChannelActivity.UIState.READY);
                        break;
                    case "NetStream.Play.PublishNotify":
                        updateView(ChannelActivity.UIState.RECEIVING);
                        if (isStreaming)
                            startListen(receiverStreamName, String.valueOf(interactor.getCounter()));
                            streamStartTime = new Date();

                        break;
                    default:
                        showToast(r5ConnectionEvent.name());
                        break;
                }
                break;
            case ERROR:
            case CLOSE:
                if (isPlaying() || isBroadcasting())
                    stopListen();
                break;
            case VIDEO_UNMUTE:
            case CONNECTED:
//                if (!isBroadcasting())
//                    bcStartTime = new Date();
            case AUDIO_UNMUTE:
                streamStartTime = new Date();
                break;
            case LICENSE_VALID:
                break;
            case TIMEOUT:
            case STOP_STREAMING:
            case AUDIO_MUTE:
            case VIDEO_MUTE:
            case LICENSE_ERROR:
            default:
                showToast(r5ConnectionEvent.name());
                break;
        }
    }

    public void stopListen() {
        long time = getTimeDelay();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                interactor.stopListen();
                updateView(ChannelActivity.UIState.READY);
            }
        }, time);
    }


    private long getTimeDelay() {
        if (bcStartTime == null)
            return 0;
        long timeDelay;
        if (streamStartTime == null)
            streamStartTime = new Date();
        timeDelay = streamStartTime.getTime() - bcStartTime.getTime();

        streamStartTime = null;
        bcStartTime = null;
        return timeDelay;
    }

    @Override
    public void notifyStatusOpenFireServer(STATE state, String message) {
        showToast(state.toString());
        switch (state) {
            case ERROR:
                mainView.showError(message);
                break;
            case CONNECTION_CLOSED:
                break;
            case RECONNECTION_SUCCESS:
                break;
            case RECONNECTION_FAILED:
                break;
            case CONNECTED:
                break;
        }
    }

    @Override
    public void notifyMessage(final String streamName, final String streamId) {
        if (!isBroadcasting()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startListen(streamName, streamId);
                }
            });
        }
        Log.d(TAG, "processMessage: " + streamName + ": " + streamId);

    }

}