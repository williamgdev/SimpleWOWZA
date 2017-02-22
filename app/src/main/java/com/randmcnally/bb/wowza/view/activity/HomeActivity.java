package com.randmcnally.bb.wowza.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.adapter.PagerAdapter;
import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.presenter.HomePresenterImpl;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.fragment.ChannelFragment;
import com.randmcnally.bb.wowza.view.fragment.DialogTextFragment;


public class HomeActivity extends AppCompatActivity implements MainView {
    private static final String TAG = "HomeActivity ->";

    private Button broadcastButton;
    private Button playButton;
    private Button stopButton;

    private ProgressBar progressBar;

    public HomePresenterImpl mainPresenter;

    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//        switch (mainPresenter.startBroadcast()){
//            case :
//            case 0:
//                updateUI(StateUI.STOP);
//                break;
//
//            case 1:
//                updateUI(StateUI.START);
//                break;
//
//        }
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
