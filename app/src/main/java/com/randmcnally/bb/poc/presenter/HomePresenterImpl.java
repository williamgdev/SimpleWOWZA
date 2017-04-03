package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.randmcnally.bb.poc.callback.ChatRoomCallback;
import com.randmcnally.bb.poc.callback.EmptyCallback;
import com.randmcnally.bb.poc.dto.openfire.ChatRoom;
import com.randmcnally.bb.poc.dto.openfire.UserRequest;
import com.randmcnally.bb.poc.network.ServiceFactory;
import com.randmcnally.bb.poc.restservice.OpenFireApiService;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.view.MainView;

import java.util.List;


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
