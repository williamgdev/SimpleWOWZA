package com.randmcnally.bb.poc.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.adapter.HistoryAdapter;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.presenter.ChannelHistoryPresenter;
import com.randmcnally.bb.poc.presenter.ChannelHistoryPresenterImpl;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

public class ChannelHistoryActivity extends BaseActivity implements ChannelHistoryView{

    private RecyclerView recyclerView;
    private HistoryAdapter channelsAdapter;
    private TextView txtError;
    private ChannelHistoryPresenter channelHistoryPresenter;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarBackIcon(R.drawable.ic_arrow_back_white);
        setToolbarTitle("History Messages");
    }

    @Override
    public void showError(String error) {
        recyclerView.setVisibility(View.GONE);
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(error);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_channel_history;
    }

    @Override
    protected int getToolbarLogoID() {
        return 0;
    }

    @Override
    protected boolean isToolBarLogoAdded() {
        return false;
    }

    @Override
    protected int getToolbarID() {
        return R.id.toolbar;
    }

    @Override
    protected boolean isToolbarAdded() {
        return true;
    }

    @Override
    protected int getProgressbarID() {
        return R.id.main_progress;
    }

    @Override
    protected boolean isProgressBarAdded() {
        return true;
    }

    @Override
    protected void initializeUIComponents() {
        recyclerView = (RecyclerView) findViewById(R.id.channel_history_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        txtError = (TextView) findViewById(R.id.channel_history_txt_error);
    }

    @Override
    protected void initializePresenter() {
        channelHistoryPresenter = new ChannelHistoryPresenterImpl();
        channelHistoryPresenter.attachView(this);
        extractBundleData(getIntent().getExtras());

    }

    private void extractBundleData(Bundle extras) {
        History history = (History) extras.getSerializable("history");
        String channelName = extras.getString("channel_name");
        channelHistoryPresenter.setHistory(history);
        channelHistoryPresenter.setChannelName(channelName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateHistory(){
        channelsAdapter = getHistoryAdapter();
        recyclerView.setAdapter(channelsAdapter);
    }

    private HistoryAdapter getHistoryAdapter() {
        historyAdapter = new HistoryAdapter(this,channelHistoryPresenter.getHistoryMessages());
        return historyAdapter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        channelHistoryPresenter.detachView();
    }
}
