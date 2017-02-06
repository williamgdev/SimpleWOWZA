package com.github.williamgdev.examples.simplewowza;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.mp4.WZMP4Writer;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class MainActivity extends AppCompatActivity implements WZStatusCallback {
    private static final String TAG = "MainActivity ->";
    // The top level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK audio device
    private WZAudioDevice goCoderAudioDevice;

    // The GoCoder SDK broadcaster
    private WZBroadcast goCoderBroadcaster;

    // The broadcast configuration settings
    private WZBroadcastConfig goCoderBroadcastConfig;
    private WZMP4Writer mp4Writer;

    private Button broadcastButton;
    private Button playButton;
    private Button stopButton;

    private File savedFile;

    private MediaPlayer mediaPlayer;

    private static String path;
    private boolean isFirst_Loading_Complete;
    private boolean is_playing_firstTime;
    private boolean isStart;

    private VideoView videoView;
    private io.vov.vitamio.widget.CenterLayout centerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        // Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-4543-0103-D362-C86D-BACE");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = new WZAudioDevice();

        // Create a broadcaster instance
        goCoderBroadcaster = new WZBroadcast();

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = new WZBroadcastConfig();

        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Cloud account
        goCoderBroadcastConfig.setHostAddress("c2a38e.entrypoint.cloud.wowza.com");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("app-2343");
        goCoderBroadcastConfig.setStreamName("e643fd43");

        // Disable video
        goCoderBroadcastConfig.setVideoEnabled(false);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

        // Use the WZMP4Writer to save the audio file while broadcasting
        mp4Writer = new WZMP4Writer();
        goCoderBroadcastConfig.registerAudioSink(mp4Writer);
        goCoderBroadcastConfig.registerVideoSink(mp4Writer);
        goCoderBroadcastConfig.setVideoEnabled(false);

        broadcastButton = (Button) findViewById(R.id.broadcast_button);
        playButton = (Button) findViewById(R.id.play_button);
        stopButton = (Button) findViewById(R.id.stop_button);
    }

    private void toggleBroadcast() {
        WZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running
            goCoderBroadcaster.endBroadcast(this);
            broadcastButton.setText(R.string.start_broadcast);
            playButton.setEnabled(true);
            stopButton.setEnabled(true);
        } else {
            // Start streaming
            broadcastButton.setText(R.string.stop_broadcast);

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File outputFile = getOutputMediaFile();
            if (outputFile != null)
                mp4Writer.setFilePath(outputFile.toString());
            else {
                Log.w(TAG, "toggleBroadcast: " + "Could not create or access the directory in which to store the MP");
                return;
            }

            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
        }
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

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // The callback invoked when an error occurs during a broadcast
    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        "Streaming error: " + goCoderStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "GoCoderSDK");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                WZLog.warn(TAG, "failed to create the directory in which to store the MP4 in " + Environment.getExternalStorageDirectory(
                ) + File.separator + "sdcard/Movies/");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        savedFile = new File(mediaStorageDir.getPath() + File.separator + "WOWZA_" + timeStamp + ".mp3");
        return savedFile;
    }

    public void playMagic(View view) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(savedFile.toString()));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }

    public void stopMagic(View view) {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void startBroadcast(View view) {
        toggleBroadcast();
    }


    public void openPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);

    }
}
