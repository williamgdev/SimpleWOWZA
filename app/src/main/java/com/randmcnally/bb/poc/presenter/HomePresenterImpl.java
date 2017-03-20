package com.randmcnally.bb.poc.presenter;

import android.content.Context;

import com.randmcnally.bb.poc.view.MainView;

public class HomePresenterImpl implements MainPresenter{
    private static final String TAG = "HomePresenterImpl";

    Context context;

    MainView mainView;

    public HomePresenterImpl(Context context) {
        this.context = context;

    }

    @Override
    public void loadData() {

    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        mainView = null;
    }

}
