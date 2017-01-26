package com.github.williamgdev.examples.simplewowza;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

public class MainActivity extends AppCompatActivity {
    private WowzaGoCoder goCoder;
    private WZCameraView cameraView;
    WowzaConfig broadcastConfig;
    private String TAG = "MainActivity => ";
    private boolean started;
    Button button;

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
        button = (Button) findViewById(R.id.main_button);

        broadcastConfig = goCoder.getConfig();
        broadcastConfig.setHostAddress("c2a38e.entrypoint.cloud.wowza.com");
        broadcastConfig.setStreamName("e643fd43");
        broadcastConfig.setUsername("client17946");
        broadcastConfig.setPassword("123");
        broadcastConfig.setPortNumber(1935);
        broadcastConfig.setApplicationName("app-2343");
        goCoder.setConfig(broadcastConfig);


        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null) {
            if (cameraView.isPreviewPaused()){
                cameraView.onResume();
            }
            else {
                cameraView.startPreview();
                Log.d(TAG, "onResume: StartPrievew");
            }
        }
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick: " + goCoder.getConfig().getHostAddress() + " - " + broadcastConfig.getHostAddress());
        WZStreamingError configValidationError = goCoder.getConfig().validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (started) {
            // Stop the broadcast that is currently running
            goCoder.endStreaming(statusCallback);
            started = false;
            button.setText(R.string.broadcast);
            Log.d(TAG, "onClick: End Streaming");
        } else {
            // Start streaming
            started = true;
            goCoder.startStreaming(cameraView.getVideoSourceConfig(), statusCallback);

            button.setText(R.string.stop_broadcast);
            Log.d(TAG, "onClick: Start Streaming");
        }
    }

    WZStatusCallback statusCallback = new WZStatusCallback() {
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
                    button.setText(R.string.broadcast);
                    started = false;
                }
            });

            Log.d(TAG, "onWZError: Status " + wzStatus.getLastError().getErrorDescription());
        }
    };
}
