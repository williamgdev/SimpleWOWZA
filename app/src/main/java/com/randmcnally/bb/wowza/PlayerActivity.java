package com.randmcnally.bb.wowza;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileNotFoundException;

public class PlayerActivity extends AppCompatActivity{

    TextView txtTitle;
    Button bClose;
    MediaPlayer mediaPlayer;
    private VideoView videoView;
    private String TAG = "PlayerActivity ->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        txtTitle = (TextView) findViewById(R.id.player_txt_title);
        bClose = (Button) findViewById(R.id.player_button_close);
        videoView = (VideoView) findViewById(R.id.player_video_view);

        /**
         * Chnage this code bellow when you are getting the audio file from the Database
         */
        Log.d(TAG, "onCreate: media url: " + Uri.parse(getIntent().getStringExtra("url")));

        mediaPlayer = MediaPlayer.create(this, Uri.parse(getIntent().getStringExtra("url")));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
//        videoView.setVisibility(View.VISIBLE);
//        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("url")));
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });

    }


    public void onPlayerClose(View view) {
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        if (videoView.isPlaying())
            videoView.stopPlayback();
        this.finish();
    }
}
