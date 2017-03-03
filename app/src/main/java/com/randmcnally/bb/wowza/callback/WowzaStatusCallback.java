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
        String statusMessage;

        switch (goCoderStatus.getState()) {
            case WZState.STARTING:
                statusMessage = "Initialization";
                break;

            case WZState.READY:
                statusMessage = "Ready";
                break;

            case WZState.RUNNING:
                statusMessage = "Streaming";
                break;

            case WZState.STOPPING:
                statusMessage = "Stopping";
                break;

            case WZState.IDLE:
                statusMessage = "Stopped";
                break;

            default:
                statusMessage = "WOWZA Status need to catch";
                break;
        }

        Log.d(TAG, "onWZStatus: " + statusMessage);
        // Display the status message using the U/I thread
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show();
//            }
//        });
        listener.notifyWowzaStatus(goCoderStatus.getState(), statusMessage);

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
        listener.notifyWowzaStatus(WZState.IDLE, goCoderStatus.getLastError().getErrorDescription());
    }

    public interface ListenerWowzaStatus{
        void notifyWowzaStatus(int state, String message);
    }
}
