package com.randmcnally.bb.poc.receiver;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.media.RingtoneManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.support.v4.app.NotificationCompat;

import com.randmcnally.bb.poc.activity.HomeActivity;

public class PushyReceiver extends BroadcastReceiver {

    private PushyReceiverListener listener;

    public void setPusyhReceiverListener(PushyReceiverListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "Breaker breaker";
        String notificationText = "Channel: ";
        boolean online = false;
        String streamName = "";

        if (intent.getStringExtra("online") != null) {
            online = intent.getBooleanExtra("success", false);
        }
        if (intent.getStringExtra("stream_name") != null) {
            streamName = intent.getStringExtra("stream_name");
            notificationText += streamName;
        }

        if (listener == null) {
            // Prepare a notification with vibration, sound and lights
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setLights(Color.RED, 1000, 1000)
                    .setVibrate(new long[]{0, 400, 250, 400})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }
        else {
            listener.notifyPushyStatus(online, streamName);
        }
    }

    public interface PushyReceiverListener{
        void notifyPushyStatus(boolean online, String streamName);
    }
}