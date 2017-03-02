package com.randmcnally.bb.wowza.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.presenter.ReceiverPresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;

public class ReceiverActivity extends AppCompatActivity implements MainView{
    private static final String TAG = "Receiver ->";
    ReceiverPresenterImpl presenter;
    private ToggleButton toggleBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        presenter = new ReceiverPresenterImpl(this, getIntent().getStringExtra("url"));
        presenter.attachView(this);
        presenter.loadData();
        toggleBroadcast = (ToggleButton) findViewById(R.id.receiver_button_start);
        toggleBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // Start Listen the RTSP
                    presenter.startListen();
                }
                else{
                    presenter.stopListen();
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
        presenter.stopListen();
        presenter.detachView();
    }


}
