package com.randmcnally.bb.wowza.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.adapter.PagerAdapter;
import com.randmcnally.bb.wowza.presenter.HomePresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;


public class HomeActivity extends AppCompatActivity implements MainView {
    private static final String TAG = "HomeActivity ->";

    private Button broadcastButton;
    private Button playButton;
    private Button stopButton;

    private ProgressBar progressBar;

    private HomePresenterImpl mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mainPresenter = new HomePresenterImpl(getApplicationContext());

        broadcastButton = (Button) findViewById(R.id.broadcast_button);
        playButton = (Button) findViewById(R.id.play_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);

    }

    public void updateUI(int state) {
        switch (state){
            case StateUI.STOP:
                broadcastButton.setText(R.string.start_broadcast);
                playButton.setEnabled(true);
                stopButton.setEnabled(true);
                break;

            case StateUI.START:
                broadcastButton.setText(R.string.stop_broadcast);
                break;

            case StateUI.ERROR:
                break;
        }
    }

    public void startBroadcast(View view) {
        mainPresenter.startBroadcast();
        switch (mainPresenter.changeStatusBroadcast()){
            case -1:
            case 0:
                updateUI(StateUI.STOP);
                break;

            case 1:
                updateUI(StateUI.START);
                break;

        }
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
    public void showMessage(String text) {

    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void showError(String error) {
        updateUI(StateUI.ERROR);
    }

    public interface StateUI {
        int STOP = 0;
        int ERROR = -1;
        int START = 1;
    }
}
