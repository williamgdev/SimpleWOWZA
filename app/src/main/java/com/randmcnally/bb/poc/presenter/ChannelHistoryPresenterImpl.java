package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.network.Red5ProInterceptor;
import com.randmcnally.bb.poc.restservice.Red5ProApiService;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelHistoryView;
import com.randmcnally.bb.poc.view.ChannelView;

import java.util.List;

import needle.Needle;

public class ChannelHistoryPresenterImpl implements ChannelHistoryPresenter {
    private static final String TAG = "ChannelHistoryPresenter ->";
    private ChannelHistoryView channelHistoryView;
    private History history;
    private OpenFireServer openFireServer;
    private String channelName;

    @Override
    public void attachView(ChannelHistoryView channelHistoryView) {
        this.channelHistoryView = channelHistoryView;
        LocalBroadcastManager.getInstance(channelHistoryView.getContext()).registerReceiver(
                localNotificationReceiver,
                new IntentFilter("RMBB_MISSED_MESSAGE")
        );
    }

    @Override
    public void detachView() {
        LocalBroadcastManager.getInstance(channelHistoryView.getContext().getApplicationContext());
        channelHistoryView = null;
    }

    @Override
    public void setHistory(History history) {
        this.history = history;
        updateAudioDuration();
    }

    @Override
    public void setOpenFireServer(OpenFireServer openFireServer) {
        this.openFireServer = openFireServer;
    }

    @Override
    public List<HistoryMessage> getHistoryMessages() {
        final List<HistoryMessage> historyMessages = ChannelUtil.convertToHistoryMessage(history.getVoiceMessages());
        return historyMessages;
    }

    @Override
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void updateHistory() {
        history = History.create(openFireServer.getGroupChatHistoryHashMap().get(channelName).getMessages());
        updateAudioDuration();
    }

    private void updateAudioDuration() {
        Red5ProApiInteractor.updateDuration(history.getVoiceMessages(),
                ((BBApplication) channelHistoryView.getContext().getApplicationContext()).IP_ADDRESS,
                new Red5ProApiInteractor.MetadataFileRetrieveListener() {
                    @Override
                    public void onSuccess() {
                        if (getHistoryMessages().size() == 0) {
                            showError("There is no messages to show");
                        } else {
                            updateUI();
                        }
                    }

                    @Override
                    public void onError(String s) {
                        Log.d(TAG, "onError: " + s);
                    }
                });
    }

    private void updateUI() {
        if (channelHistoryView != null) {
            ((Activity) channelHistoryView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    channelHistoryView.updateHistory();
                }
            });
        }
    }

    private void showError(final String message) {
        if (channelHistoryView != null) {
            ((Activity) channelHistoryView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    channelHistoryView.showError(message);
                }
            });
        }

    }

    private BroadcastReceiver localNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LiveStream live_stream = (LiveStream) intent.getExtras().getSerializable("live_stream");
            if (channelName.equals(live_stream.getStreamName())){
                updateHistory();
            }
        }
    };


}
