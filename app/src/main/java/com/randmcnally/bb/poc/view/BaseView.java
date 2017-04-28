package com.randmcnally.bb.poc.view;

import android.content.Context;

public interface BaseView {
    void showProgress();
    void hideProgress();
    void showError(String error);
    Context getContext();
}

