package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.callback.StatusLiveStreamCallback;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.restservice.PushyAPI;
import com.randmcnally.bb.poc.view.MainView;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadcastPresenterImpl implements MainPresenter, BBPlayer.ListenerBBPlayer,
        R5ConnectionListener, StatusLiveStreamCallback.LiveStreamListener{
    private static final String TAG = "Broadcast ->";
    private final StatusLiveStreamCallback liveStreamCallback;

    MainView mainView;
    Context context;
    ChannelInteractor interactor;
    String message;

    private BBPlayer bbPlayer;
    private boolean isStreaming;


    public BroadcastPresenterImpl(Context context, ChannelInteractor interactor) {
        this.context = context;
        this.interactor = interactor;
        interactor.setR5ConnectionListener(this);

        liveStreamCallback = new StatusLiveStreamCallback(this);

        try {
            bbPlayer = new BBPlayer("", this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadData() {
        updateView(ChannelActivity.UIState.READY);
//        startListen();
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

    public void startBroadcast() {
        String fileName = interactor.startBroadcast(); //Send the filename to the Receiver
        updateView(ChannelActivity.UIState.BROADCASTING);
    }

    public void stopBroadcast() {
        updateView(ChannelActivity.UIState.READY);
        interactor.stop();
    }

    public boolean isBroadcasting() {
        return interactor.isBroadcasting();
    }

    public String getMessage() {
        return message;
    }

    public boolean isPlaying() {
        if (bbPlayer != null)
            return bbPlayer.isPlaying();
        return false;
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
    public void onListener(BBPlayer.BBPLAYER state) {
        switch (state) {
            case PLAYING:
                updateView(ChannelActivity.UIState.RECEIVING);
                break;

            case STOPPED:
            case ERROR_UNKNOWN:
            case AUDIO_STREAM_COMPLETED:
                showToast(state.toString());
                updateView(ChannelActivity.UIState.READY);
                break;

            case AUDIO_STREAM_END:
                Toast.makeText(context, "CREATE A STATE AUDIO_STREAM_END", Toast.LENGTH_SHORT).show();
                break;
            case AUDIO_STREAM_START:
                Toast.makeText(context, "CREATE A STATE AUDIO_STREAM_START", Toast.LENGTH_SHORT).show();
                break;
            case INFO_UNKNOWN:
                Toast.makeText(context, "CREATE A STATE INFO_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case PREPARING:
                Toast.makeText(context, "CREATE A STATE PREPARING", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    public void startListen() {
        if (interactor.isBroadcasting()){
            interactor.stop();
        }
        if (!isStreaming){
            interactor.checkStream(liveStreamCallback);
        }
    }


    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        showToast(r5ConnectionEvent.message);
        switch (r5ConnectionEvent) {
            case CONNECTED:
                break;
            case DISCONNECTED:
                showToast(r5ConnectionEvent.message);
                updateView(ChannelActivity.UIState.READY);
                break;
            case ERROR:
                break;
            case TIMEOUT:
                break;
            case CLOSE:
                break;
            case START_STREAMING:
                break;
            case STOP_STREAMING:
                break;
            case NET_STATUS:
                break;
            case AUDIO_MUTE:
                break;
            case AUDIO_UNMUTE:
                break;
            case VIDEO_MUTE:
                break;
            case VIDEO_UNMUTE:
                break;
            case LICENSE_ERROR:
                break;
            case LICENSE_VALID:
                break;
        }
    }


    @Override
    public void notifyLiveStreamStatus(StatusLiveStreamCallback.LiveStreamListener.STATUS status, String message) {
        switch (status) {
            case ERROR:
            case FAILURE:
                isStreaming = false;
                if (interactor.isCheckingStream() && !interactor.isListening()) {
                    startListen();
                }
                else if (interactor.isListening()){
                    interactor.stop();
                    updateView(ChannelActivity.UIState.READY);
                }
                break;
            case CONNECTED:
                if (interactor.isCheckingStream()){
                    isStreaming = true;
                    if (interactor.isListening()) {
//                        interactor.checkStream(liveStreamCallback);
                    }
                    else {
                        interactor.stopCheckStream();
                        interactor.play();
                        updateView(ChannelActivity.UIState.RECEIVING);
                    }
                }
                break;
        }
    }

    public void stopListen() {
        interactor.stopListen();
    }
}
