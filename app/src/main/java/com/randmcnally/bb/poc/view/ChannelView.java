package com.randmcnally.bb.poc.view;

import android.media.AudioManager;

import com.randmcnally.bb.poc.activity.ChannelActivity;

public interface ChannelView extends BaseView {
    void showMessage(String text);
    void updateView(ChannelActivity.UIState state);

    AudioManager getAudioManager();

    void setMicrophoneMute(boolean b);

    void playBipSound();

    enum UIState {
        LOADING, READY, BROADCASTING, RECEIVING, CONFlICT, BROADCASTING_PREPARING, BROADCASTING_STOPPING, ERROR
    }
}
