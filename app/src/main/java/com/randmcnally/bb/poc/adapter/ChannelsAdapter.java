package com.randmcnally.bb.poc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.view.ChannelFragmentView;

import java.util.List;
import java.util.StringTokenizer;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder> {
    private final ChannelFragmentView view;
    Context context;
    List<Channel> channels;

    public ChannelsAdapter(Context context, ChannelFragmentView view,  List<Channel> channels) {
        this.view = view;
        this.context = context;
        this.channels = channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.channel_item, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChannelViewHolder holder, final int position) {
        holder.setName(channels.get(position).getName());
        if (channels.get(position).getHistory() != null)
            holder.setNumber(channels.get(position).getHistory().getMissedMessages().size());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.onChannelSelected(channels.get(position));
            }
        });
    }

    public Channel getChannel(int position) {
        return channels.get(position);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        TextView txtMissedMessages;

        public ChannelViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.channel_item_name);
            txtMissedMessages = (TextView) view.findViewById(R.id.channel_badge_missed_messages);
        }

        public void setName(String name) {
            txtName.setText(name);
        }
        public void setNumber(int number) {
            if (number != 0){
                txtMissedMessages.setVisibility(View.VISIBLE);
                txtMissedMessages.setText(String.valueOf(number));
            }
            else{
                txtMissedMessages.setVisibility(View.INVISIBLE);
            }
        }

    }

}
