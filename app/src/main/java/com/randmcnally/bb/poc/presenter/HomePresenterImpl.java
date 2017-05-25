package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.interactor.NotificationService;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelView;
import com.randmcnally.bb.poc.view.HomeView;


public class HomePresenterImpl implements HomePresenter, OpenFireServer.OpenFireServerListener {
    private static final String TAG = "HomePresenterImpl ->";

    private HomeView homeView;
    private DatabaseInteractor databaseInteractor;
    private LiveStream streamReceived;
    private OpenFireServer openFireServer;

    @Override
    public void attachView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void detachView() {
        homeView = null;
    }

    @Override
    public void setOpenFireServer(final OpenFireServer openFireServer) {
        homeView.showProgress();
        this.openFireServer = openFireServer;
        openFireServer.connectOpenFireServer(this);
        onStartService();
    }

    private void onStartService() {
        Intent intent = new Intent(homeView.getContext(), NotificationService.class);
        homeView.getContext().startService(intent);
    }

    @Override
    public void notifyStatusOpenFireServer(STATE state, String message) {
        switch (state) {
            case ERROR:
                break;
            case CONNECTION_CLOSED:
                break;
            case RECONNECTION_SUCCESS:
                break;
            case RECONNECTION_FAILED:
                break;
            case AUTHENTICATED:
                hideProgress();
                updateUI(ChannelView.UIState.READY);
                Log.d(TAG, "notifyStatusOpenFireServer: OpenFire Authenticated Successfully");
                break;
            case NOT_AUTHORIZED:
                String uniqueID = FileUtil.getDeviceUID(homeView.getContext());

                OpenFireApiInteractor apiManager = OpenFireApiInteractor.getInstance(((BBApplication) homeView.getContext().getApplicationContext()).IP_ADDRESS);
                apiManager.createUser(new UserRequest(uniqueID, uniqueID), new OpenFireApiInteractor.CreateUserApiListener() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "notifyStatusOpenFireServer: User created successfully");
                        onOpenFireReconnect();
                    }

                    @Override
                    public void onError(String s) {
                        showToast(s);
                    }
                });

                break;
            case CONNECTED:
                break;
        }
    }

    private void onOpenFireReconnect() {
        openFireServer.connectOpenFireServer(this);
    }

    @Override
    public void setDatabaseInteractor(DatabaseInteractor databaseInteractor) {
        this.databaseInteractor = databaseInteractor;
    }

    private void showToast(final String message){
        if (homeView != null) {
            ((Activity) homeView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homeView.getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateUI(final ChannelView.UIState state) {
        if (homeView != null) {
            ((Activity) homeView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeView.updateView(state);
                }
            });
        }
    }

    private void hideProgress() {
        if (homeView != null) {
            ((Activity) homeView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeView.hideProgress();
                }
            });
        }
    }
}
