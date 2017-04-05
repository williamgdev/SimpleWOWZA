package com.randmcnally.bb.poc.presenter;

import android.content.Context;

import com.randmcnally.bb.poc.view.BaseView;
import com.randmcnally.bb.poc.view.MainView;


public class HomePresenterImpl implements HomePresenter {
    private static final String TAG = "HomePresenterImpl";

    Context context;

    MainView mainView;

    public HomePresenterImpl(Context context) {
        this.context = context;

    }

    @Override
    public void attachView(BaseView baseView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        mainView = null;
    }

}
