package com.randmcnally.bb.poc.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.adapter.ChannelsAdapter;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.presenter.ChannelFragmentPresenter;
import com.randmcnally.bb.poc.presenter.ChannelFragmentPresenterImpl;
import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends BaseFragment implements DialogTextFragment.ListenerDialogFragment, ChannelFragmentView {
//    private FloatingActionButton fab;
    ChannelFragmentPresenter presenter;
    RecyclerView recyclerView;
    DialogTextFragment dialogFragment;
    ChannelsAdapter channelsAdapter;


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_channel;
    }

    @Override
    protected void initializeUIComponents(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.channel_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
//        fab = (FloatingActionButton) view.findViewById(R.id.channel_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog();
//            }
//        });
    }

//    private void showDialog() {
//        dialogFragment = new DialogTextFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), "channel_dialog_text");
//        dialogFragment.setListenerDialogFragment(this);
//
//    }

/**
 * openReceiverActivity method is deprecated
  */
//    public static void openReceiverActivity(Context context, String url) {
//        Intent intent = new Intent(context, ReceiverActivity.class);
//        intent.putExtra("url", url);
//        context.startActivity(intent);
//    }

    public static void openChannelActivity(Context context, Channel channel) {
        Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra("stream_name", channel.getStreamName());
        intent.putExtra("channel_name", channel.getName());
        context.startActivity(intent);
    }

    @Override
    public void sendText(String text) {
        /**
         * Uncomment when the Add Channel is available
         */
//        presenter.addChannel(text);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initializePresenter() {
        presenter = new ChannelFragmentPresenterImpl();
        presenter.attachView(this);
        presenter.registerDevice();
        presenter.getChannels();

    }

    @Override
    public void setChannels(List<Channel> channels) {
        channelsAdapter = getChannelAdapter(channels);
        recyclerView.setAdapter(channelsAdapter);
    }

    private ChannelsAdapter getChannelAdapter(List<Channel> channels) {
        return new ChannelsAdapter(getActivity(), channels);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
