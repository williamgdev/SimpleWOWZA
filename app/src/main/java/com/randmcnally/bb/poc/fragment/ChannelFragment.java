package com.randmcnally.bb.poc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.adapter.ChannelsAdapter;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.presenter.ChannelFragmentPresenterImpl;
import com.randmcnally.bb.poc.view.ChannelView;
import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.view.MainView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements MainView, DialogTextFragment.ListenerDialogFragment{
//    private FloatingActionButton fab;
    ChannelFragmentPresenterImpl presenter;
    RecyclerView recyclerView;
    DialogTextFragment dialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_channel, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.channel_recycler_view);

        presenter = new ChannelFragmentPresenterImpl(this);
//        updateUI();
//        fab = (FloatingActionButton) view.findViewById(R.id.channel_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog();
//            }
//        });
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
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String text) {

    }

    @Override
    public void updateView(ChannelActivity.UIState state) {

    }
}
