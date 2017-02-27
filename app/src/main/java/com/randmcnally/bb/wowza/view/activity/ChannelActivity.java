package com.randmcnally.bb.wowza.view.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.presenter.BroadcastPresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.custom.PlayGifView;

public class ChannelActivity extends AppCompatActivity implements MainView {
    BroadcastPresenterImpl presenter;
    TextView txtState;
    ImageView imgSpeak, imgBroadcast, iconBroadcast;
    LinearLayout layoutBroadcast, layoutSpeaker;
    PlayGifView gifLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        txtState = (TextView) findViewById(R.id.channel_txt_state);
        imgBroadcast = (ImageView) findViewById(R.id.channel_img_broadcast);
        imgSpeak = (ImageView) findViewById(R.id.channel_img_microphone);
        gifLoading = (PlayGifView) findViewById(R.id.channel_view_gif);
        gifLoading.setImageResource(R.drawable.gif_loading);
        iconBroadcast = (ImageView) findViewById(R.id.channel_icon_broadcast);
        layoutBroadcast = (LinearLayout) findViewById(R.id.channel_layout_broadcast);
        layoutSpeaker = (LinearLayout) findViewById(R.id.channel_layout_speaker);
        layoutBroadcast.setOnTouchListener(broadcastTouchListener);

        updateUI(UIState.LOADING);

        presenter = new BroadcastPresenterImpl(this, getIntent().getStringExtra("stream_name"));
        presenter.attachView(this);

        //Start the Stream as soon as possible
        presenter.loadData();

    }

    public void updateUI(UIState state) {
        switch (state) {
            case LOADING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);
                txtState.setText(R.string.loading);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_disabled);
                break;

            case READY:
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_ready);
                txtState.setText(R.string.ready);
                txtState.setTextColor(Color.BLACK);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_ready);
                layoutBroadcast.setBackgroundColor(getResources().getColor(R.color.colorGray));

                break;

            case BROADCASTING:
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_disabled);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_sending);
                layoutBroadcast.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                txtState.setText(R.string.broadcasting);
                txtState.setTextColor(Color.WHITE);

                break;

            case RECEIVING:
                txtState.setText(R.string.receiving);
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_enabled);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_disabled);
                layoutSpeaker.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                layoutBroadcast.setBackgroundColor(getResources().getColor(R.color.colorGray));
                break;
            case CONFlICT:
                break;
        }
    }


    private View.OnTouchListener broadcastTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();

            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);

            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();
            int action = event.getAction();

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    // TODO use data
                    // start broadcasting
                    if (!presenter.isBroadcasting()) {
                        presenter.startBroadcast();
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: { // a pointer was moved
                    // TODO use data
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    // TODO use data

                    // TODO use data
                    // stop BC stream
                    if (presenter.isBroadcasting())
                        presenter.stopBroadcast();
                    break;
                }
            }

            return true;
        }
    };

    @Override
    public void showMessage(String text) {

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
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter.isBroadcasting())
            presenter.stopBroadcast();
        presenter.stopListen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter.isStreaming())
            presenter.stopStream();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    public enum UIState {
        LOADING, READY, BROADCASTING, RECEIVING, CONFlICT;
    }

}
