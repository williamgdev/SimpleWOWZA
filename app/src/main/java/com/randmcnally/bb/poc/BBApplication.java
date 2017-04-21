package com.randmcnally.bb.poc;

import android.app.Application;
import android.content.Context;

import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;


public class BBApplication extends Application {
    DatabaseInteractor interactor;
    OpenFireServer openFireServer;

    public DatabaseInteractor getDatabaseInteractor(Context context) {
        interactor = DatabaseInteractor.getInstance(context);
        return interactor;
    }

    public OpenFireServer getOpenFireServer(Context context){
        String uniqueUID = FileUtil.getDeviceUID(context);
        openFireServer = OpenFireServer.getInstance(uniqueUID);
        return openFireServer;
    }
}
