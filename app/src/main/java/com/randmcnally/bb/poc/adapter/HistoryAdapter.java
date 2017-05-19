package com.randmcnally.bb.poc.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.R;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.state.MessagePause;
import com.randmcnally.bb.poc.state.MessagePlay;
import com.randmcnally.bb.poc.state.MessageStop;
import com.randmcnally.bb.poc.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements BBPlayer.ListenerPlaylistBBPlayer{
    private static final String TAG = "HistoryAdapter ->";
    private final List<HistoryMessage> history;
    private final Context context;
    private BBPlayer bbPlayer;
    private HistoryMessage currentPlayMessage;

    public HistoryAdapter(Context context, List<HistoryMessage> history) {
        this.context = context;
        this.history = history;

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

    public HistoryMessage getCurrentPlayMessage() {
        return currentPlayMessage;
    }

    public BBPlayer getBBPlayer(){
        if (bbPlayer == null || bbPlayer.isReleased()) {
            try {
                bbPlayer = new BBPlayer(
                        Playlist.createFromMessage(history, currentPlayMessage),
                        ((BBApplication) context.getApplicationContext()).IP_ADDRESS,
                        this);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Player found a error!", Toast.LENGTH_SHORT).show();
            }
        }
        return bbPlayer;
    }



    @Override
    public void onListener(BBPlayer.BBPLAYERSTATE state) {
            switch (state) {
                case PLAYING:
                    if (currentPlayMessage.getState() instanceof MessageStop) {
                        Log.d(TAG, "onListener: PLAYING");
                        currentPlayMessage = history.get(currentPlayMessage.getPosition() + 1);
                        currentPlayMessage.setState(new MessagePlay(this));
                        this.notifyItemChanged(currentPlayMessage.getPosition());
                    }

                    break;
                case AUDIO_STREAM_COMPLETED:
                    break;
                case AUDIO_STREAM_END:
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

    @Override
    public void onMessageCompleted(VoiceMessage voiceMessage) {
        Log.d(TAG, "onMessageCompleted: ");
        if (currentPlayMessage.getVoicemessage().equals(voiceMessage)){
            currentPlayMessage.setState(new MessageStop(bbPlayer));
            this.notifyItemChanged(currentPlayMessage.getPosition());
        } else {
            String s = "";
        }
    }

    public Context getContext() {
        return context;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            seekBar = (SeekBar) itemView.findViewById(R.id.channel_history_seek_bar); //TODO update the seek bar when the audio file is playing
            imagePlayButton = (ImageView) itemView.findViewById(R.id.channel_history_play_button);
            imagePlayButton.setOnClickListener(this);
            txtNameMessage.setOnClickListener(this);
        }

        public void setText(String name) {
            txtNameMessage.setText(name);
        }

        public void bindData(HistoryMessage historyMessage) {
            this.historyMessage = historyMessage;
            setText(historyMessage.getVoicemessage().getName());
            if (currentPlayMessage != null) {
                checkMessageAction();
            } else {
                String s = "";
            }
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.channel_history_play_button:
                case R.id.channel_history_txt_name:
                    if (! (this.historyMessage.getState() instanceof MessagePlay)) {
                        play();
                    } else {
                        pause();
                    }
                    break;
            }
        }

        private void checkMessageAction() {
            if (historyMessage.getState() instanceof MessagePlay) {
                play();
            } else if (historyMessage.getState() instanceof MessageStop){
                stop();
            } else if(historyMessage.getState() instanceof MessagePause){
                pause();
            }
        }

        private void pause() {
            imagePlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            this.historyMessage.setState(new MessagePause(historyAdapter.getBBPlayer()));
            historyMessage.action();
        }

        private void play() {
            if (currentPlayMessage != null && !(currentPlayMessage.getVoicemessage().equals(historyMessage.getVoicemessage()))) {
                currentPlayMessage.setState(new MessageStop(bbPlayer));
                currentPlayMessage.action();
                historyAdapter.notifyItemChanged(currentPlayMessage.getPosition());
            }
            currentPlayMessage = historyMessage;
            this.historyMessage.setState(new MessagePlay(historyAdapter));
            historyMessage.action();
            seekBar.setMax(historyMessage.getDuration());
            imagePlayButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            durationHandler.post(updateSeekBarTime);
            //TODO make sure the presenter has updated the history message
        }

        private void stop() {
            imagePlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            durationHandler.removeCallbacks(updateSeekBarTime);
            historyMessage.action();
            seekBar.setProgress(6000);
        }

        final Handler durationHandler = new Handler(Looper.getMainLooper());
        private Runnable updateSeekBarTime = new Runnable() {
            public void run() {

                //set seekbar progress using time played
                if (bbPlayer.getTimeElapsed() > 0)
                    seekBar.setProgress(bbPlayer.getTimeElapsed());

                durationHandler.postDelayed(this, 100);
            }
        };
    }
}
