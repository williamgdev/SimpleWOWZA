package com.randmcnally.bb.poc.interactor;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;


public class NotificationService extends Service {
    private static final String TAG = "NotificationServices ->";
    private OpenFireServer openFireServer;

    public void setOpenFireServer() {
        this.openFireServer = OpenFireServer.getInstance(FileUtil.getDeviceUID(getApplicationContext()), ((BBApplication)getApplication()).IP_ADDRESS, listener);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        setOpenFireServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private OpenFireServer.OpenFireMessageListener listener = new OpenFireServer.OpenFireMessageListener() {
        @Override
        public void notifyMessage(String streamName, String streamId) {
            LiveStream streamReceived = new LiveStream(streamName, Integer.parseInt(streamId));
            Log.d(TAG, "notifyMessage: " + ChannelUtil.getPublishName(streamName, streamId));
            sendMissedMessageBroadcast(streamReceived);
        }
    };


    private void sendMissedMessageBroadcast(LiveStream streamReceived){
        Bundle bundle = new Bundle();
        bundle.putSerializable("live_stream", streamReceived);
        Intent intent = new Intent("RMBB_MISSED_MESSAGE");
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}
