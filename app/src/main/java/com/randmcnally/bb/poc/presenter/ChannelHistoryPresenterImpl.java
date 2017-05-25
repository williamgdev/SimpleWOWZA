package com.randmcnally.bb.poc.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

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

import java.util.List;

import needle.Needle;

public class ChannelHistoryPresenterImpl implements ChannelHistoryPresenter {
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
        if (getHistoryMessages().size() == 0) {
            channelHistoryView.showError("There is no messages to show");
        } else {
            channelHistoryView.updateHistory();
        }
    }

    @Override
    public void setOpenFireServer(OpenFireServer openFireServer) {
        this.openFireServer = openFireServer;
    }

    @Override
    public List<HistoryMessage> getHistoryMessages() {
        final List<HistoryMessage> historyMessages = ChannelUtil.convertToHistoryMessage(history.getVoiceMessages());

        Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Red5ProApiInteractor.updateDuration(historyMessages,((BBApplication)channelHistoryView.getContext().getApplicationContext()).IP_ADDRESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return historyMessages;
    }

    @Override
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void updateHistory() {
        history = History.create(openFireServer.getGroupChatHistoryHashMap().get(channelName).getMessages());
        channelHistoryView.updateHistory();
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
