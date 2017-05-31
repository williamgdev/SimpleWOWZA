package com.randmcnally.bb.poc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.interactor.TimerInteractor;
import com.randmcnally.bb.poc.state.MessagePause;
import com.randmcnally.bb.poc.state.MessagePlay;
import com.randmcnally.bb.poc.state.MessageStop;

import java.io.IOException;
import java.util.List;

import needle.Needle;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements BBPlayer.ListenerBBPlayer{
    private static final String TAG = "HistoryAdapter ->";
    private final List<HistoryMessage> history;
    private final Context context;
    private TimerInteractor timerInteractor;
    private final LinearLayoutManager layoutManager;
    private HistoryMessage currentPlayMessage;
    private BBPlayer bbPlayer;
    private SeekBar seekBar;

    public HistoryAdapter(Context context, List<HistoryMessage> history, LinearLayoutManager layoutManager) {
        this.context = context;
        this.history = history;
        this.layoutManager = layoutManager;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.channel_history_item, parent, false);
        return new HistoryViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, final int position) {
        holder.bindData(history.get(position));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }


    public Context getContext() {
        return context;
    }

    public void pauseMessage(HistoryMessage historyMessage) {
        currentPlayMessage = historyMessage;
        if (bbPlayer.isPlaying()) {
            bbPlayer.pause();
        }
        if (historyMessage.getPosition() >= layoutManager.findFirstVisibleItemPosition() &&
                historyMessage.getPosition() <= layoutManager.findLastVisibleItemPosition()) {
            timerInteractor.pause();
        }
    }

    public void playHistoryMessage(HistoryMessage historyMessage, SeekBar seekBar) {
        this.seekBar = seekBar;
        currentPlayMessage = historyMessage;
        timerInteractor = TimerInteractor.getInstance(Math.round(currentPlayMessage.getVoicemessage().getTimeMilliseconds()) + 100, 10);
        try {
            bbPlayer = new BBPlayer(
                    Red5ProApiInteractor.getURLStream(historyMessage.getVoicemessage().getName(), ((BBApplication) context.getApplicationContext()).IP_ADDRESS),
                    this);
            bbPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "pauseMessage: ", e.getCause());
        }
    }

    public void stopHistoryMessage(HistoryMessage historyMessage) {
        currentPlayMessage = historyMessage;
        bbPlayer.stop();
    }

    @Override
    public void onListener(BBPlayer.BBPLAYERSTATE state) {
        switch (state) {
            case PLAYING:
                break;
            case AUDIO_STREAM_COMPLETED:
                currentPlayMessage.setState(new MessageStop(this));
                if (currentPlayMessage.getPosition() >= layoutManager.findFirstVisibleItemPosition() &&
                        currentPlayMessage.getPosition() <= layoutManager.findLastVisibleItemPosition()) {
                    timerInteractor.stop();
                }
                notifyItemChanged(currentPlayMessage.getPosition());
                if (currentPlayMessage.getPosition() < history.size() - 1){ //play the next Message
                    HistoryMessage nextMessage = history.get(currentPlayMessage.getPosition() + 1);
                    nextMessage.setState(new MessagePlay(this));
                    notifyItemChanged(nextMessage.getPosition());
                }
                break;
            case AUDIO_STREAM_END:
                if (currentPlayMessage.getPosition() >= layoutManager.findFirstVisibleItemPosition() &&
                        currentPlayMessage.getPosition() <= layoutManager.findLastVisibleItemPosition()) {
                    try {
                        timerInteractor.play(seekBar, bbPlayer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onListener: Error");
                    }
                }
                break;
            case AUDIO_STREAM_START:
                break;
            case INFO_UNKNOWN:
                break;
            case ERROR_UNKNOWN:
                break;
            case STOPPED:
                break;
            case PLAYLIST_EMPTY:
                break;
            case PREPARING:
                break;
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private final HistoryAdapter historyAdapter;
        LinearLayout layoutControl;
        TextView txtNameMessage;
        SeekBar seekBar;
        ImageView imagePlayButton;
        private HistoryMessage historyMessage;

        public HistoryViewHolder(View itemView, HistoryAdapter historyAdapter) {
            super(itemView);
            this.historyAdapter = historyAdapter;
            layoutControl = (LinearLayout) itemView.findViewById(R.id.channel_history_layout_control);
            txtNameMessage = (TextView) itemView.findViewById(R.id.channel_history_txt_name);
            seekBar = (SeekBar) itemView.findViewById(R.id.channel_history_seek_bar);
            imagePlayButton = (ImageView) itemView.findViewById(R.id.channel_history_play_button);
            imagePlayButton.setOnClickListener(this);
            txtNameMessage.setOnClickListener(this);
        }

        public void bindData(final HistoryMessage historyMessage) {
            this.historyMessage = historyMessage;
            txtNameMessage.setText(historyMessage.getVoicemessage().getName());
            seekBar.setMax((int) historyMessage.getVoicemessage().getTimeMilliseconds());
            if (currentPlayMessage != null) {
                historyMessage.getState().updateUI(this);
            }

        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.channel_history_play_button:
                case R.id.channel_history_txt_name:
                    if ((historyMessage.getState() instanceof MessagePlay)) {
                        historyMessage.setState(new MessagePause(historyAdapter));
                    } else {
                        if (currentPlayMessage != null &&
                                ! currentPlayMessage.getVoicemessage().equals(historyMessage.getVoicemessage()) &&
                                currentPlayMessage.getState() instanceof MessagePlay){
                            currentPlayMessage.setState(new MessageStop(historyAdapter));
                            currentPlayMessage.getState().updateUI(this);
                        }
                        historyMessage.setState(new MessagePlay(historyAdapter));
                    }
                    historyMessage.getState().updateUI(this);
                    break;
            }
        }

        public void pause() {
            imagePlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            historyMessage.action(seekBar);
        }

        public void play() {
            imagePlayButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            historyMessage.action(seekBar);
        }

        public void stop() {
            imagePlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            historyMessage.action(seekBar);
            seekBar.setProgress((int) historyMessage.getVoicemessage().getTimeMilliseconds());
        }

    }
}
