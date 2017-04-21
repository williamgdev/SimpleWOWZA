package com.randmcnally.bb.poc.view;


public interface HomeView extends BaseView {
    void showMessage(String text);
    void updateView(ChannelView.UIState state);
}
