package com.randmcnally.bb.wowza;

public interface BaseView {
    void showProgress();
    void hideProgress();
    void showError(String error);
}

