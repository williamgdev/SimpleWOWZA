package com.randmcnally.bb.poc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.presenter.ChannelHistoryPresenter;
import com.randmcnally.bb.poc.view.ChannelHistoryView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private final Context context;
    private final ChannelHistoryPresenter channelHistoryPresenter;

    public HistoryAdapter(Context context, ChannelHistoryPresenter channelHistoryPresenter) {
        this.context = context;
        this.channelHistoryPresenter = channelHistoryPresenter;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.channel_history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, final int position) {
        holder.setText(channelHistoryPresenter.getMessageName(position));
        holder.setOnClickListener(position);
    }

    @Override
    public int getItemCount() {
        return channelHistoryPresenter.getMessageSize();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutControl;
        TextView txtNameMessage;
        SeekBar seekBar;
        ImageView imagePlayButton;
        boolean iconPause;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            layoutControl = (LinearLayout) itemView.findViewById(R.id.channel_history_layout_control);
            txtNameMessage = (TextView) itemView.findViewById(R.id.channel_history_txt_name);
            seekBar = (SeekBar) itemView.findViewById(R.id.channel_history_seek_bar); //TODO update the seek bar when the audio file is playing
            imagePlayButton = (ImageView) itemView.findViewById(R.id.channel_history_play_button);
        }

        public void setText(String name) {
            txtNameMessage.setText(name);
        }

        public void switchIconControl(int position) {
            if (iconPause) {
                imagePlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                channelHistoryPresenter.onMessagePause(position, seekBar);
            } else {
                imagePlayButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                channelHistoryPresenter.onMessagePlay(position, seekBar);
            }
            iconPause = !iconPause;
        }

        public void setOnClickListener(final int position) {
            layoutControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getContext() instanceof ChannelHistoryView) {
                        switchIconControl(position);
                    }
                }
            });
        }
    }
}
