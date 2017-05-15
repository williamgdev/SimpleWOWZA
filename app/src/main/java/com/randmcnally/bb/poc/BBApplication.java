package com.randmcnally.bb.poc;

import android.app.Application;
import android.content.Context;

import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;


public class BBApplication extends Application {
    public static final String IP_ADDRESS = "192.168.43.212";
//    public static final String IP_ADDRESS = "52.10.208.192";
//    public static final String IP_ADDRESS = "ec2-52-10-208-192.us-west-2.compute.amazonaws.com";
    DatabaseInteractor interactor;
    OpenFireServer openFireServer;

    public DatabaseInteractor getDatabaseInteractor(Context context) {
        interactor = DatabaseInteractor.getInstance(context);
        return interactor;
    }

    public OpenFireServer getOpenFireServer(Context context){
        String uniqueUID = FileUtil.getDeviceUID(context);
        openFireServer = OpenFireServer.getInstance(uniqueUID, IP_ADDRESS);
        return openFireServer;
    }
}
