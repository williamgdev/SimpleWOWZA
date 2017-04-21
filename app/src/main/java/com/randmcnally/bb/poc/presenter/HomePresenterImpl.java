package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
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

    @Override
    public void setOpenFireServer(OpenFireServer openFireServer) {
        openFireServer.setListener(new OpenFireServer.OpenFireServerListener() {
            @Override
            public void notifyStatusOpenFireServer(STATE state, String message) {
                switch (state) {
                    case ERROR:
//                        String uniqueID = FileUtil.getDeviceUID(channelFragmentView.getContext());
//
//                        apiManager = OpenFireApiInteractor.getInstance();
//                        apiManager.createUser(new UserRequest(uniqueID, uniqueID), new OpenFireApiInteractor.CreateUserApiListener() {
//                            @Override
//                            public void onSuccess(String s) {
//                                showToast(s);
//                            }
//
//                            @Override
//                            public void onError(String s) {
//                                showToast(s);
//                            }
//                        });
                        break;
                    case CONNECTION_CLOSED:
                        break;
                    case RECONNECTION_SUCCESS:
                        break;
                    case RECONNECTION_FAILED:
                        break;
                    case AUTHENTICATED:
                        showToast("Connected");
                        break;
                    case CONNECTED:
                        break;
                }
            }

        });

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
}
