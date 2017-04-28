package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.BaseView;
import com.randmcnally.bb.poc.view.ChannelView;
import com.randmcnally.bb.poc.view.HomeView;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;
import java.util.List;


public class HomePresenterImpl implements HomePresenter {
    private static final String TAG = "HomePresenterImpl ->";

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
    public void setOpenFireServer(final OpenFireServer openFireServer) {
        homeView.showProgress();
        openFireServer.connectOpenFireServer();

        openFireServer.setListener(new OpenFireServer.OpenFireServerListener() {
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
                        openFireServer.join(new OpenFireServer.OpenFireListener<HashMap<MultiUserChat, List<Message>>>() {
                            @Override
                            public void onSuccess(HashMap<MultiUserChat, List<Message>> result) {
                                /**
                                 * TODO Cash the channel on the database
                                 */
                                Log.d(TAG, "onSuccess: Join");
                                hideProgress();
                                updateUI(ChannelView.UIState.READY);
                            }

                            @Override
                            public void onError(String message) {
                                showToast(message);
                            }
                        });
                        Log.d(TAG, "notifyStatusOpenFireServer: OpenFire Authenticated Successfully");
                        break;
                    case NOT_AUTHORIZED:
                        String uniqueID = FileUtil.getDeviceUID(homeView.getContext());

                        OpenFireApiInteractor apiManager = OpenFireApiInteractor.getInstance();
                        apiManager.createUser(new UserRequest(uniqueID, uniqueID), new OpenFireApiInteractor.CreateUserApiListener() {
                            @Override
                            public void onSuccess(String s) {
                                Log.d(TAG, "notifyStatusOpenFireServer: User created successfully");
                                openFireServer.connectOpenFireServer();
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

            @Override
            public void notifyMessage(String streamName, String streamId) {
                Log.d(TAG, "notifyMessage: " + streamName + "_" + streamId);
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
