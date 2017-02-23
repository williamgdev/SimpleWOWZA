package com.randmcnally.bb.wowza.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.callback.StreamStatusCallback;
import com.randmcnally.bb.wowza.callback.WowzaStatusCallback;
import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.network.ServiceFactory;
import com.randmcnally.bb.wowza.restservice.ApiService;
import com.randmcnally.bb.wowza.util.FileUtil;
import com.randmcnally.bb.wowza.view.MainView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;

import java.io.File;

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
