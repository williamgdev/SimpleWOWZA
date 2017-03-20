package com.randmcnally.bb.poc.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.view.MainView;

public class PlayerActivity extends AppCompatActivity implements MainView {

    TextView txtTitle;
    Button bClose;
    private String TAG = "PlayerActivity ->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        txtTitle = (TextView) findViewById(R.id.player_txt_title);
        bClose = (Button) findViewById(R.id.player_button_close);



    }


    @Override
    public void showMessage(String text) {

    }

    @Override
    public void updateView(ChannelActivity.UIState state) {
        // Update the UI here
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {

    }
}
