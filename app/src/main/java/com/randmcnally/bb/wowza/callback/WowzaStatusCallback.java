package com.randmcnally.bb.wowza.callback;


import android.util.Log;

import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;


public class WowzaStatusCallback implements WZStatusCallback {
    private static final String TAG = "WowzaStatusCallback";
    ListenerWowzaStatus listener;

    public WowzaStatusCallback(ListenerWowzaStatus listener) {
        this.listener = listener;
    }

    // The callback invoked upon changes to the state of the steaming broadcast
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {
            case WZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        Log.d(TAG, "onWZStatus: " + statusMessage);
        // Display the status message using the U/I thread
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show();
//            }
//        });
        listener.notifyWowzaStatus(goCoderStatus.getState(), statusMessage.toString());

    }

    // The callback invoked when an error occurs during a broadcast
    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        Log.d(TAG, "onWZError: " + goCoderStatus.getLastError().getErrorDescription());
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context,
//                        "Streaming error: " + goCoderStatus.getLastError().getErrorDescription(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
        listener.notifyWowzaStatus(WZState.STOPPING, goCoderStatus.getLastError().getErrorDescription());
    }

    public interface ListenerWowzaStatus{
        void notifyWowzaStatus(int state, String message);
    }
}
