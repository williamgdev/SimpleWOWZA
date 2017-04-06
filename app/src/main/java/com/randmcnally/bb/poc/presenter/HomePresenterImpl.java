package com.randmcnally.bb.poc.presenter;

import android.content.Context;

import com.randmcnally.bb.poc.view.BaseView;
import com.randmcnally.bb.poc.view.HomeView;


public class HomePresenterImpl implements HomePresenter {
    private static final String TAG = "HomePresenterImpl";

    HomeView homeView;

    @Override
    public void attachView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void detachView() {
        homeView = null;
    }

}
