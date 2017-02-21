package com.randmcnally.bb.wowza.view;

public interface BaseView {
    void showProgress();
    void hideProgress();
    void showError(String error);
}

