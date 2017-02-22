package com.randmcnally.bb.wowza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randmcnally.bb.wowza.R;
import com.randmcnally.bb.wowza.database.Channel;

import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>{
    Context context;
    List<Channel> channels;

    public ChannelsAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    public void setChannels(List<Channel> channels){
        this.channels = channels;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.channel_item, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        holder.setName(channels.get(position).getName());
    }

    public Channel getChannel(int position){
        return channels.get(position);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;

        public ChannelViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.channel_item_name);
        }

        public void setName(String name){
            txtName.setText(name);
        }
    }
}
