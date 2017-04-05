package com.randmcnally.bb.poc.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.custom.BBTouchListener;
import com.randmcnally.bb.poc.presenter.ChannelPresenter;
import com.randmcnally.bb.poc.presenter.ChannelPresenterImpl;
import com.randmcnally.bb.poc.view.ChannelView;
import com.randmcnally.bb.poc.custom.PlayGifView;


public class ChannelActivity extends BaseActivity implements ChannelView{
    ChannelPresenter presenter;
    TextView txtState;
    ImageView imgSpeak, imgBroadcast, iconBroadcast;
    LinearLayout layoutBroadcast, layoutSpeaker;
    RelativeLayout layoutText;
    PlayGifView gifLoading;
    UIState currentState;

//    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setToolbarTitle(getIntent().getStringExtra("channel_name"));

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
        setToolbarBackIcon(R.drawable.ic_arrow_back_white);
//        actionbar.setHomeAsUpIndicator ( R.drawable.ic_action_back );

        updateUI(UIState.LOADING);


        presenter = new ChannelPresenterImpl(this,
                getIntent().getStringExtra("stream_name"),
                getIntent().getStringExtra("channel_name"));
        presenter.attachView(this);

        //Start the Stream as soon as possible
        presenter.loadData();

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_channel;
    }

    @Override
    protected int getToolbarID() {
        return R.id.toolbar;
    }

    @Override
    protected boolean isToolbarAdded() {
        return true;
    }

    @Override
    protected int getProgressbarID() {
        return R.id.main_progress;
    }

    @Override
    protected boolean isProgressBarAdded() {
        return true;
    }

    @Override
    protected void initializeUIComponents() {

    }

    @Override
    protected boolean isToolBarLogoAdded() {
        return false;
    }

    @Override
    protected int getToolbarLogoID() {
        return 0;
    }

    @Override
    protected void setToolbarTitle(String title) {
        titleToolBar.setText(title);
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
            case BROADCASTING_PREPARING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);
                iconBroadcast.setVisibility(View.VISIBLE);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_sending);
                layoutBroadcast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                layoutText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                txtState.setText(R.string.loading);
                txtState.setTextColor(Color.WHITE);
                layoutBroadcast.setEnabled(true);
                break;

            case BROADCASTING_STOPPING:
                gifLoading.setVisibility(View.VISIBLE);
                imgSpeak.setVisibility(View.GONE);
                imgSpeak.setImageResource(R.drawable.icon_speaker_ready);
                txtState.setText(R.string.loading);
                txtState.setTextColor(Color.BLACK);
                imgBroadcast.setImageResource(R.drawable.icon_microphone_disabled);
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
            }
        }

        @Override
        public void oNTouchEnd() {
            if (presenter.isBroadcasting() || presenter.isPreparing()) {
                presenter.stopBroadcast();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
