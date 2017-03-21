package com.randmcnally.bb.poc.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.callback.PushyCallback;
import com.randmcnally.bb.poc.custom.BBTouchListener;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.network.ServiceFactory;
import com.randmcnally.bb.poc.presenter.BroadcastPresenterImpl;
import com.randmcnally.bb.poc.receiver.PushyReceiver;
import com.randmcnally.bb.poc.restservice.NotificationServiceAPI;
import com.randmcnally.bb.poc.restservice.PushyAPI;
import com.randmcnally.bb.poc.view.MainView;
import com.randmcnally.bb.poc.custom.PlayGifView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.Pushy;

public class ChannelActivity extends AppCompatActivity implements MainView{
    BroadcastPresenterImpl presenter;
    TextView txtState, txtTitle;
    ImageView imgSpeak, imgBroadcast, iconBroadcast, iconToolBar;
    LinearLayout layoutBroadcast, layoutSpeaker;
    RelativeLayout layoutText;
    PlayGifView gifLoading;
    UIState currentState;

//    private ActionBar actionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Pushy.listen(this);
        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setContentView(R.layout.activity_channel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtTitle = (TextView) findViewById(R.id.toolbar_txt_title);
        txtTitle.setText(getIntent().getStringExtra("channel_name"));
        iconToolBar = (ImageView) findViewById(R.id.toolbar_icon);
        iconToolBar.setVisibility(View.GONE);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        txtState = (TextView) findViewById(R.id.channel_txt_state);
        imgBroadcast = (ImageView) findViewById(R.id.channel_img_broadcast);
        imgSpeak = (ImageView) findViewById(R.id.channel_img_microphone);
        gifLoading = (PlayGifView) findViewById(R.id.channel_view_gif);
        gifLoading.setImageResource(R.drawable.gif_loading);
        iconBroadcast = (ImageView) findViewById(R.id.channel_icon_broadcast);
        layoutBroadcast = (LinearLayout) findViewById(R.id.channel_layout_broadcast);
        layoutSpeaker = (LinearLayout) findViewById(R.id.channel_layout_speaker);
        layoutText = (RelativeLayout) findViewById(R.id.channel_layout_text);
        layoutBroadcast.setOnTouchListener(bbTouchListener);

//        actionbar.setHomeAsUpIndicator ( R.drawable.ic_action_back );

        updateUI(UIState.LOADING);


        presenter = new BroadcastPresenterImpl(this, new ChannelInteractor(
                getIntent().getStringExtra("stream_name"),
                getIntent().getStringExtra("channel_name"))
        );
        presenter.attachView(this);

        //Start the Stream as soon as possible
        presenter.loadData();

        new RegisterForPushNotificationsAsync().execute();

        LocalBroadcastManager.getInstance(this).registerReceiver(mSelectionReceiver, new IntentFilter("pushy.me"));
    }

    private BroadcastReceiver mSelectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String online = "";

            if (intent.getStringExtra("online") != null) {
                online = intent.getStringExtra("online");
            }
            if (online.equals("true")) {
                presenter.startListen();
            }
            else {
                presenter.stopListen();

            }
        }
    };

    String deviceToken;
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getApplicationContext());

                // Log it for debugging purposes
                Log.d("MyApp", "Pushy device token: " + deviceToken);

                // Send the token to your backend server via an HTTP GET request
                new URL("https://{YOUR_API_HOSTNAME}/register/device?token=" + deviceToken).openConnection();
            }
            catch (Exception exc) {
                // Return exc to onPostExecute
                return exc;
            }

            // Success
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc) {
            // Failed?
            if (exc != null) {
                // Show error as toast message
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            // Succeeded, do something to alert the user
        }
    }

    public static final String SECRET_API_KEY = "8cc782d407f0f3830391081a8e3633b5adfb924c1b1dc8d1d1c9977c656b75d6";
    public static final String BASE_URL = "https://api.pushy.me/";

    public void sendNotification(boolean b) {
        if (deviceToken == null){
            Log.d("MyApp", "sendNotification: Error registration");
            return;
        }
        // Prepare list of target device tokens
        List<String> deviceTokens = new ArrayList<String>();

        // Add your device tokens here
        if (deviceToken.equals("b2e2a47764542343d732e9"))
            deviceTokens.add("3f58671baede6ab28bcd8c");
        else
            deviceTokens.add("b2e2a47764542343d732e9");

        // Set payload (any object, it will be serialized to JSON)
        Map<String, String> payload = new HashMap<String, String>();

        // Add "message" parameter to payload
//        payload.put("online", String.valueOf(presenter.isBroadcasting()));
        payload.put("online", String.valueOf(b));
        payload.put("stream_name", txtTitle.getText().toString());

        // Prepare the push request
        PushyAPI.PushyPushRequest push = new PushyAPI.PushyPushRequest(
                payload,
                deviceTokens.toArray(new String[deviceTokens.size()])
        );

        NotificationServiceAPI serviceAPI = ServiceFactory.createNotificationAPIService(BASE_URL);
        serviceAPI.sendNotification(push, SECRET_API_KEY).enqueue(new PushyCallback(new PushyCallback.PushyListener() {
            @Override
            public void errorPushy(String message) {
                Log.d("MyApp", "errorPushy: " + message);
            }
        }));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI(UIState state) {
        currentState = state;
        switch (state) {
            case LOADING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);
                txtState.setText(R.string.loading);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_disabled);
                break;

            case READY:
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.icon_speaker_ready);
                txtState.setText(R.string.ready);
                txtState.setTextColor(Color.BLACK);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_ready);
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutSpeaker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                iconBroadcast.setVisibility(View.INVISIBLE);
                break;

            case BROADCASTING:
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.icon_speaker_disabled);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_sending);
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                txtState.setText(R.string.broadcasting);
                txtState.setTextColor(Color.WHITE);

                break;

            case RECEIVING:
                txtState.setText(R.string.receiving);
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.icon_speaker_enabled);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_disabled);
                layoutSpeaker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));
                txtState.setTextColor(Color.WHITE);
                break;

            case ERROR:
                showError(presenter.getMessage());
                break;
        }
    }

    private BBTouchListener bbTouchListener = new BBTouchListener() {

        public void oNTouchStart() {
            if (!presenter.isBroadcasting() && !presenter.isPlaying() && currentState != UIState.LOADING) {
                presenter.startBroadcast();
                sendNotification(true);
            }
        }

        @Override
        public void oNTouchEnd() {
            if (presenter.isBroadcasting()) {
                presenter.stopBroadcast();
                sendNotification(false);
            }

        }
    };

    @Override
    public void showMessage(String text) {
        txtState.setText(text);
    }

    @Override
    public void updateView(UIState state) {
        updateUI(state);
    }

    @Override
    public void showProgress() {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {
        Toast.makeText(this, "Load Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        txtState.setText(error);
        layoutSpeaker.setVisibility(View.GONE);
        layoutBroadcast.setVisibility(View.GONE);
        iconBroadcast.setVisibility(View.INVISIBLE);
        imgSpeak.setVisibility(View.INVISIBLE);
        imgBroadcast.setVisibility(View.INVISIBLE);
        gifLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachView();
        if (presenter.isBroadcasting())
            presenter.stopBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    public enum UIState {
        LOADING, READY, BROADCASTING, RECEIVING, CONFlICT, ERROR
    }

}
