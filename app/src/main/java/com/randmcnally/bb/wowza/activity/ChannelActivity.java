package com.randmcnally.bb.wowza.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.custom.BBTouchListener;
import com.randmcnally.bb.wowza.presenter.BroadcastPresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.custom.PlayGifView;

public class ChannelActivity extends AppCompatActivity implements MainView {
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
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorGray), PorterDuff.Mode.SRC_ATOP);
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


        presenter = new BroadcastPresenterImpl(this,
                getIntent().getStringExtra("stream_name"),
                getIntent().getStringExtra("code_stream"),
                getIntent().getStringExtra("rtsp_url"),
                getIntent().getStringExtra("m3u8_url"),
                getIntent().getStringExtra("host_name"),
                getIntent().getStringExtra("app_name"));
        presenter.attachView(this);

        //Start the Stream as soon as possible
        presenter.loadData();


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
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_disabled);
                layoutBroadcast.setEnabled(false);
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
                layoutBroadcast.setEnabled(true);
                break;

            case BROADCASTING:
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_disabled);
//                iconBroadcast.setVisibility(View.VISIBLE);
//                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_sending);
//                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
//                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                txtState.setText(R.string.broadcasting);
//                txtState.setTextColor(Color.WHITE);
//                layoutBroadcast.setEnabled(true);

                break;

            case BROADCASTING_PREPARING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);
//                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_disabled);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_sending);
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                txtState.setText(R.string.loading);
                txtState.setTextColor(Color.WHITE);
                layoutBroadcast.setEnabled(true);



                break;

            case BROADCASTING_STOPPING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);


//                gifLoading.setVisibility(View.GONE);
//                imgSpeak.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.icon_speaker_ready);
                txtState.setText(R.string.loading);
                txtState.setTextColor(Color.BLACK);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_disabled);
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutSpeaker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                iconBroadcast.setVisibility(View.INVISIBLE);
                layoutBroadcast.setEnabled(true);

                break;

            case RECEIVING:
                txtState.setText(R.string.receiving);
                gifLoading.setVisibility(View.GONE);
                imgSpeak.setVisibility(View.VISIBLE);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgSpeak.setImageResource(R.drawable.ic_icon_speaker_enabled);
                imgBroadcast.setImageResource(R.drawable.ic_icon_microphone_disabled);
                layoutSpeaker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));
                txtState.setTextColor(Color.WHITE);
                layoutBroadcast.setEnabled(false);
                break;

            case ERROR:
                showError(presenter.getMessage());
                break;
        }
    }

    private String TAG = "Channel ->";
    private BBTouchListener bbTouchListener = new BBTouchListener() {
        @Override
        public void oNTouchStart() {
            if (!presenter.isBroadcasting() && !presenter.isPlaying() && currentState != UIState.LOADING) {
                presenter.startBroadcast();
                Log.d(TAG, "oNTouchStart: ");
            }
        }

        @Override
        public void oNTouchEnd() {
            if (presenter.isBroadcasting()){
                presenter.stopBroadcast();
                Log.d(TAG, "oNTouchStop: ");
            }
        }
    };

    @Override
    public void showMessage(String text) {
        //txtState.setText(text);
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
        presenter.stopStream();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter.isStreaming())
            presenter.stopStream();
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
        LOADING, READY, BROADCASTING, RECEIVING, CONFlICT, BROADCASTING_PREPARING, BROADCASTING_STOPPING, ERROR
    }

}
