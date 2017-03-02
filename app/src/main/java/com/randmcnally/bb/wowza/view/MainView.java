package com.randmcnally.bb.wowza.view;

import com.randmcnally.bb.wowza.activity.ChannelActivity;

public interface MainView extends BaseView {
    void showMessage(String text);
    void updateView(ChannelActivity.UIState state);
}
