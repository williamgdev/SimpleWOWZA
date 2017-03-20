package com.randmcnally.bb.poc.view;

import com.randmcnally.bb.poc.activity.ChannelActivity;

public interface MainView extends BaseView {
    void showMessage(String text);
    void updateView(ChannelActivity.UIState state);
}
