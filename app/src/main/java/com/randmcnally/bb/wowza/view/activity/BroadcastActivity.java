package com.randmcnally.bb.wowza.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.presenter.BroadcastPresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;

public class BroadcastActivity extends AppCompatActivity implements MainView {
    ToggleButton toggleBroadcast;
    BroadcastPresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        presenter = new BroadcastPresenterImpl(this, getIntent().getStringExtra("stream_name"));
        presenter.attachView(this);
        toggleBroadcast = (ToggleButton) findViewById(R.id.broadcast_button_start);
        toggleBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // Start BroadCast
                    presenter.startBroadcast();
                }
                else{
                    presenter.stopBroadcast();
                }
            }
        });

    }

    @Override
    public void showMessage(String text) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


//    public void updateUI(int state) {
//        switch (state){
//            case StateUI.STOP:
//                broadcastButton.setText(R.string.start_broadcast);
//                playButton.setEnabled(true);
//                stopButton.setEnabled(true);
//                break;
//
//            case StateUI.START:
//                broadcastButton.setText(R.string.stop_broadcast);
//                break;
//
//            case StateUI.ERROR:
//                break;
//        }
//    }
//
//    public interface StateUI {
//        int STOP = 0;
//        int ERROR = -1;
//        int START = 1;
//    }

}
