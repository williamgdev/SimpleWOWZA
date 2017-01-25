package com.github.williamgdev.examples.simplewowza;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

public class MainActivity extends AppCompatActivity implements WZStatusCallback{
    private WowzaGoCoder goCoder;
    private WZCameraView cameraView;
    WZBroadcast broadcast;
    WZBroadcastConfig broadcastConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-4543-0103-D362-C86D-BACE");
        if(goCoder == null){
            WZError wzError = WowzaGoCoder.getLastError();
            Toast.makeText(this, "GoCodeError SDK error: " + wzError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            return;
        }
        cameraView = (WZCameraView) findViewById(R.id.main_camera_view);
        broadcast = new WZBroadcast();
        broadcastConfig = new WZBroadcastConfig(WZMediaConfig.FRAME_SIZE_640x480);
        broadcastConfig.setHostAddress("https://cloud.wowza.com/en/xprjdp7v/manage/live_streams/wh9x1kcp");
        broadcastConfig.setStreamName("testwowza");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null) {
            if (cameraView.isPreviewPaused()){
                cameraView.onResume();
            }
            else
                cameraView.startPreview();
        }
    }

    public void onClick(View view) {
        WZStreamingError configValidationError = broadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (broadcast.getStatus().isRunning()) {
            // Stop the broadcast that is currently running
            broadcast.endBroadcast(this);
        } else {
            // Start streaming
            broadcast.startBroadcast(broadcastConfig);
        }
    }

    @Override
    public void onWZStatus(WZStatus wzStatus) {
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (wzStatus.getState()) {
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
        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onWZError(final WZStatus wzStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        "Streaming error: " + wzStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
