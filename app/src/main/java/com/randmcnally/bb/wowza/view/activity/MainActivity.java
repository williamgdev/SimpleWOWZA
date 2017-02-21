package com.randmcnally.bb.wowza.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.presenter.MainPresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;


public class MainActivity extends AppCompatActivity implements MainView {
    private static final String TAG = "MainActivity ->";

    private Button broadcastButton;
    private Button playButton;
    private Button stopButton;

    private MainPresenterImpl mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenterImpl(getApplicationContext());

        broadcastButton = (Button) findViewById(R.id.broadcast_button);
        playButton = (Button) findViewById(R.id.play_button);
        stopButton = (Button) findViewById(R.id.stop_button);
    }

    public void startBroadcast(View view) {
        switch (mainPresenter.changeStatusBroadcast()){
            case -1:
            case 0:
                stopStateUI();
                break;

            case 1:
                broadcastButton.setText(R.string.stop_broadcast);
                break;

        }
    }

    public void stopStateUI() {
        broadcastButton.setText(R.string.start_broadcast);
        playButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public void openPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", mainPresenter.getUrlStream());
        startActivity(intent);
    }

    public void playMagic(View view) {
        mainPresenter.startListen();
    }

    public void stopMagic(View view) {
        mainPresenter.stopListen();
    }

    @Override
    public void showText(String text) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {
        switch (error){
            case "Error":
                stopStateUI();
                break;
        }
    }
}
