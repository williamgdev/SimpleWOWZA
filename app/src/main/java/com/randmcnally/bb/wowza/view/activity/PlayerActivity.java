package com.randmcnally.bb.wowza.view.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.view.MainView;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity implements MainView {

    TextView txtTitle;
    Button bClose;
    MediaPlayer mediaPlayer;
    private String TAG = "PlayerActivity ->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        txtTitle = (TextView) findViewById(R.id.player_txt_title);
        bClose = (Button) findViewById(R.id.player_button_close);

        Log.d(TAG, "onCreate: media url: " + Uri.parse(getIntent().getStringExtra("url")));

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getIntent().getStringExtra("url"));
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

    }

    private void stopPlayer() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void onPlayerClose(View view) {
        this.finish();
        stopPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
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

    }
}
