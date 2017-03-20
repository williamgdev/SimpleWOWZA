package com.randmcnally.bb.poc.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.adapter.PagerAdapter;
import com.randmcnally.bb.poc.presenter.HomePresenterImpl;
import com.randmcnally.bb.poc.view.MainView;


public class HomeActivity extends AppCompatActivity implements MainView {
    private static final String TAG = "HomeActivity ->";

    private ProgressBar progressBar;

    public HomePresenterImpl presenter;

    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
//        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
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

        presenter = new HomePresenterImpl(getApplicationContext());
        presenter.attachView(this);

        progressBar = (ProgressBar) findViewById(R.id.main_progress);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void showMessage(String text) {

    }

    @Override
    public void updateView(ChannelActivity.UIState state) {
        // Update here the UI
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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
