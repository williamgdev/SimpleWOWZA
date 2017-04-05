package com.randmcnally.bb.poc.view;

public interface ChannelView extends MainView {
    enum UIState {
        LOADING, READY, BROADCASTING, RECEIVING, CONFlICT, BROADCASTING_PREPARING, BROADCASTING_STOPPING, ERROR
    }
}
