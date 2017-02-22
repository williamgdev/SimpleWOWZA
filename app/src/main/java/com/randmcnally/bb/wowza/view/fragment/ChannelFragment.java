package com.randmcnally.bb.wowza.view.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.adapter.ChannelsAdapter;
import com.randmcnally.bb.wowza.presenter.ChannelPresenterImpl;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment {
    private FloatingActionButton fab;
    ChannelPresenterImpl presenter;
    RecyclerView recyclerView;

    public ChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_channel, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.channel_recycler_view);

        presenter = new ChannelPresenterImpl(getContext());

        fab = (FloatingActionButton) view.findViewById(R.id.channel_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTextFragment dialogFragment = new DialogTextFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager(), "channel_dialog_text");
            }
        });
        return view;
    }

    public void updateUI() {
        ChannelsAdapter mAdapter = new ChannelsAdapter(getContext(), presenter.getChannels());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }
}
