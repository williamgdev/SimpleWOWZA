package com.randmcnally.bb.poc.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.view.BaseView;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    protected TextView titleToolBar;
    protected ImageView iconToolBar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        setToolbar();
        setToolBarLogo();
        setProgressBar();

        initializeUIComponents();

    }

    protected void setToolbarTitle(String title){
        if (isToolbarAdded()) {
            titleToolBar.setText(getTitle().toString());
        }
    }

    public void setToolbar() {
        if (!isToolbarAdded()) {
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(getToolbarID());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        titleToolBar = (TextView) findViewById(R.id.toolbar_txt_title);
        titleToolBar.setText(getTitle().toString());

    }

    public void setToolBarLogo() {
        if (!isToolBarLogoAdded()){
            return;
        }

        iconToolBar = (ImageView) findViewById(getToolbarLogoID());
        iconToolBar.setVisibility(View.VISIBLE);
    }

    public void setToolbarBackIcon(@DrawableRes int drawableID){
        if (!isToolbarAdded()) {
            return;
        }
        final Drawable upArrow = ContextCompat.getDrawable(this, drawableID);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    public void setProgressBar(){
        if (!isProgressBarAdded()) {
            return;
        }
        progressBar = (ProgressBar) findViewById(getProgressbarID());
    }


    @Override
    public void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @IdRes
    protected abstract int getToolbarLogoID();

    @LayoutRes
    protected abstract int getLayoutID();

    @IdRes
    protected abstract int getToolbarID();

    protected abstract boolean isToolbarAdded();

    @IdRes
    protected abstract int getProgressbarID();

    protected abstract boolean isProgressBarAdded();

    protected abstract void initializeUIComponents();

    protected abstract boolean isToolBarLogoAdded();

}
