package com.randmcnally.bb.wowza.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.adapter.ChannelsAdapter;
import com.randmcnally.bb.wowza.presenter.ChannelPresenterImpl;
import com.randmcnally.bb.wowza.view.ChannelView;
import com.randmcnally.bb.wowza.view.MainView;
import com.randmcnally.bb.wowza.view.activity.BroadcastActivity;
import com.randmcnally.bb.wowza.view.activity.PlayerActivity;
import com.randmcnally.bb.wowza.view.activity.ReceiverActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements ChannelView, DialogTextFragment.ListenerDialogFragment{
    private FloatingActionButton fab;
    ChannelPresenterImpl presenter;
    RecyclerView recyclerView;
    DialogTextFragment dialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_channel, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.channel_recycler_view);

        presenter = new ChannelPresenterImpl(this);
        updateUI();
        fab = (FloatingActionButton) view.findViewById(R.id.channel_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        return view;
    }

    private void showDialog() {
        dialogFragment = new DialogTextFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "channel_dialog_text");
        dialogFragment.setListenerDialogFragment(this);

    }

    public void updateUI() {
        ChannelsAdapter mAdapter = new ChannelsAdapter(getContext(), presenter.getChannels());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    public static void openReceiverActivity(Context context, String url) {
        Intent intent = new Intent(context, ReceiverActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void openBroadcastActivity(Context context, String streamName) {
        Intent intent = new Intent(context, BroadcastActivity.class);
        intent.putExtra("stream_name", streamName);
        context.startActivity(intent);
    }

    @Override
    public void sendText(String text) {
        presenter.addChannel(text);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}
