package com.randmcnally.bb.poc.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.adapter.PagerAdapter;
import com.randmcnally.bb.poc.presenter.BasePresenter;
import com.randmcnally.bb.poc.presenter.HomePresenter;
import com.randmcnally.bb.poc.presenter.HomePresenterImpl;
import com.randmcnally.bb.poc.view.BaseView;
import com.randmcnally.bb.poc.view.ChannelView;
import com.randmcnally.bb.poc.view.HomeView;


public class HomeActivity extends BaseActivity implements HomeView {
    private static final String TAG = "HomeActivity ->";


    public HomePresenter presenter;

    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected int getToolbarLogoID() {
        return R.id.toolbar_icon ;
    }

    @Override
    protected boolean isToolBarLogoAdded() {
        return true;
    }

    @Override
    public HomePresenter getPresenter() {
        return presenter;
    }

    @Override
    public HomeView getView() {
        return this;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected int getToolbarID() {
        return R.id.toolbar;
    }

    @Override
    protected boolean isToolbarAdded() {
        return false;
    }

    @Override
    protected int getProgressbarID() {
        return R.id.home_progress;
    }

    @Override
    protected boolean isProgressBarAdded() {
        return true;
    }

    @Override
    protected void initializeUIComponents() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Channels"));
//        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        updateView(ChannelView.UIState.LOADING);

    }

    private void setAdapter() {
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
    public void updateView(ChannelView.UIState state) {
        // Update here the UI
        switch (state){
            case LOADING:
                break;
            case READY:
                setAdapter();
                break;
        }
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void initializePresenter() {
        presenter = new HomePresenterImpl();
        presenter.attachView(this);
        presenter.setDatabaseInteractor(((BBApplication) getApplication()).getDatabaseInteractor(this));
        presenter.setOpenFireServer(((BBApplication) getApplication()).getOpenFireServer(this));

    }

}
