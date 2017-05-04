package com.randmcnally.bb.poc.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dao.ChannelEntity;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelView;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ChannelPresenterImpl implements ChannelPresenter, ChannelInteractor.ChannelInteractorListener {
    private static final String TAG = "Broadcast ->";

    ChannelView channelView;
    ChannelInteractor channelInteractor;
    DatabaseInteractor databaseInteractor;
    String message;
    private int counter;

    static boolean preparing;

    Date streamStartTime;
    Date bcStartTime;
    Red5ProApiInteractor red5ProApiInteractor;

    History red5ProHistory;
    private Channel activeChannel;
    private OpenFireServer openFireServer;
    private MultiUserChat currentChat;

    public ChannelPresenterImpl(Channel channel) {
        /**
         * TODO Get the id from the last stream created
         */
        this.activeChannel = channel;
        this.activeChannel.setLiveStream(new LiveStream(channel.getRoomId(), 0)); //the stream name is created with the same name as a roomId
        this.channelInteractor = ChannelInteractor.getInstance(channel, this);
        red5ProApiInteractor = Red5ProApiInteractor.getInstance();
        red5ProApiInteractor.getRecordedFiles(new Red5ProApiInteractor.RecordedFileApiListener() {
            @Override
            public void onSuccess(History history) {
                setRed5ProHistory(history);
            }

            @Override
            public void onError(String s) {

            }
        });

    }

    public void setRed5ProHistory(History red5ProHistory) {
        this.red5ProHistory = red5ProHistory;
    }

    private BroadcastReceiver localNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("online") != null) {
                String online = intent.getStringExtra("online");

                if (online.equals("true")) {
                    if (intent.getStringExtra("stream_name") != null && intent.getStringExtra("stream_id") != null) {
                        String streamName = intent.getStringExtra("stream_name");
                        int streamId = Integer.parseInt(intent.getStringExtra("stream_id"));

                        if (activeChannel.getLiveStream().getStreamName().equals(streamName)) {
                            activeChannel.setLiveStream(new LiveStream(intent.getStringExtra("stream_name"), streamId));
                            startListen(activeChannel.getLiveStream());
                        } else {
                            /**
                             * TODO Save the message in the Muted Channel
                             */
                            showToast("The Channel is not active");
                        }
                    } else {
                        showToast("Error: No stream name received");
                    }
                } else {
                }
            }
        }
    };

    @Override
    public boolean isPreparing() {
        return preparing;
    }

    @Override
    public void loadData() {
        updateView(ChannelView.UIState.READY);

    }

    @Override
    public void attachView(ChannelView mainView) {
        this.channelView = mainView;
        loadData();
    }

    @Override
    public void detachView() {
        LocalBroadcastManager.getInstance(channelView.getContext()).unregisterReceiver(localNotificationReceiver);
        if (channelInteractor.isListening())
            channelInteractor.stopListen();
        if (isBroadcasting())
            stopBroadcast();
        channelView = null;
    }

    @Override
    public void setDatabaseInteractor(DatabaseInteractor databaseInteractor) {
        this.databaseInteractor = databaseInteractor;
    }

    @Override
    public void setOpenFireServer(final OpenFireServer openFireServer) {
        this.openFireServer = openFireServer;
        openFireServer.getGroupChatRoom(activeChannel.getRoomId(), new OpenFireServer.OpenFireListener<MultiUserChat>() {
            @Override
            public void onSuccess(MultiUserChat chat) {
                setCurrentChat(chat);
            }

            @Override
            public void onError(String message) {
                showToast(message);
            }
        });
        this.openFireServer.setListener(new OpenFireServer.OpenFireServerListener() {
            @Override
            public void notifyStatusOpenFireServer(STATE state, String message) {
                
            }

            @Override
            public void notifyMessage(final String streamName, final String streamId) {
                final LiveStream streamReceived = new LiveStream(streamName, Integer.parseInt(streamId));
                if (activeChannel.getLiveStream().getStreamName().equals(streamName)){
                    if (!isBroadcasting()){
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * TODO
                                 *
                                 * 2- remove the messages depends of the limit declared in the stream. actual is 5
                                 * 3- check all channels and notify the user the message for each one
                                 * 4- create a state to know when the message is already listened. No start listen the user message
                                 * 5- check the file already exists
                                 */

                                activeChannel.setLiveStream(streamReceived);
                                startListen(activeChannel.getLiveStream());
                                Log.d(TAG, "notifyMessage: " + streamReceived.getPublishStreamName());

                            }
                        });
                    } else {
                        notifyListenedMessage(streamReceived);
                    }
                } else {
                    notifyMissedMessage(streamReceived);
                }

            }
        });
    }

    private void notifyMissedMessage(final LiveStream streamReceived) {
        databaseInteractor.readByNameOrCreate(streamReceived.getStreamName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity history) {
                if (history.getId() != null) {
                    VoiceMessageEntity voiceMessageEntity = new VoiceMessageEntity(ChannelUtil.getPublishName(streamReceived.getStreamName(), streamReceived.getId()));
                    addListenedMessages(history, voiceMessageEntity); //TODO create a Add/Remove VoiceMessage
                }
            }
        });
    }

    private void notifyListenedMessage(final LiveStream streamReceived) {
        databaseInteractor.readByNameOrCreate(streamReceived.getStreamName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity history) {
                if (history.getId() != null) {
                    VoiceMessageEntity voiceMessageEntity = new VoiceMessageEntity(ChannelUtil.getPublishName(streamReceived.getStreamName(), streamReceived.getId()));
                    removeListenedMessages(history, voiceMessageEntity); //TODO create a Add/Remove VoiceMessage
                }
            }
        });
    }

    @Override
    public int getMissedMessages() {
        return activeChannel.getHistory().getMissedMessages().size();
    }

    @Override
    public void startBroadcast() {
        bcStartTime = new Date();

        preparing = true;
        if (channelView.getAudioManager().isMicrophoneMute() == false) {
            channelView.setMicrophoneMute(true);
        }
        updateView(ChannelView.UIState.BROADCASTING_PREPARING);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preparing) {
                    preparing = false;
                    channelView.playBipSound();
                    channelView.setMicrophoneMute(false);

                    channelInteractor.startBroadcast(nextCounter()); //Send the filename to the Receiver

                    updateView(ChannelView.UIState.BROADCASTING);
                }
            }
        }, 500);
    }

    @Override
    public void stopBroadcast() {
        if (preparing) {
            preparing = false;
            updateView(ChannelView.UIState.READY);
            if (activeChannel.getHistory().getMissedMessages() != null) {
                try {
                    final BBPlayer bbPlayer = new BBPlayer(activeChannel.getHistory().getMissedMessages(), new BBPlayer.ListenerPlaylistBBPlayer() {
                        @Override
                        public void onMessageCompleted(final VoiceMessage voiceMessage) {
                            Log.d(TAG, "onListener: MESSAGE_COMPLETE");
                            databaseInteractor.readByNameOrCreate(activeChannel.getName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
                                @Override
                                public void onResult(HistoryEntity history) {
                                    if (history.getId() != null) {
                                        VoiceMessageEntity voiceMessageEntity = VoiceMessageEntity.create(voiceMessage);
                                        addListenedMessages(history, voiceMessageEntity);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onListener(BBPlayer.BBPLAYERSTATE state) {
                            switch (state) {
                                case PLAYING:
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
                                    updateView(ChannelView.UIState.NO_MISSED_MESSAGE);
                                    break;
                                case PREPARING:
                                    break;
                            }
                        }
                    });
                    bbPlayer.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        updateView(ChannelView.UIState.BROADCASTING_STOPPING);
        long time = getTimeDelay();

        channelView.setMicrophoneMute(true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                channelInteractor.stop();
                channelView.setMicrophoneMute(false);
                updateView(ChannelView.UIState.READY);
            }
        }, time);

    }


    @Override
    public boolean isBroadcasting() {
        return channelInteractor.isBroadcasting();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isPlaying() {
        return channelInteractor.isListening();
    }

    void updateView(final ChannelView.UIState state) {
        if (channelView != null) {
            ((Activity) channelView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    channelView.updateView(state);
                }
            });
        }
    }

    private void showToast(final String message) {
        if (channelView != null) {
            ((Activity) channelView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(channelView.getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void startListen(LiveStream liveStream) {
        activeChannel.setLiveStream(liveStream);
        bcStartTime = new Date();
        if (channelInteractor.isBroadcasting()) {
            channelInteractor.stop();
        }
        if (!isPlaying()) {
            channelInteractor.play(liveStream);
            updateView(ChannelView.UIState.RECEIVING);
        }
    }

    private int nextCounter() {
        counter++;
        return getCounter();
    }

    public int getCounter() {
        if (counter > 5)
            counter = 0;
        return counter;
    }

    private void removeListenedMessages(HistoryEntity history, VoiceMessageEntity voiceMessage) {
        List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(history);
        voiceMessages.remove(voiceMessage);

        String json = ChannelUtil.getJsonFromHistory(history, voiceMessages);

        databaseInteractor.update(history.getId(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                Log.d(TAG, "onResult: Saved Voice Messages " + result.getId() + " - " + result.getMessages().toString());
            }
        }, json);

    }

    private void addListenedMessages(HistoryEntity history, VoiceMessageEntity voiceMessage) {
        List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(history);
        voiceMessages.add(voiceMessage);

        /**
         * Todo get limit of message globally. Actual is 5.
         */
        if (voiceMessages.size() > 5) {
            voiceMessages.remove(0);
            // if limit reached, remove the first one
            // because the first one will be the oldest.
        }

        String json = ChannelUtil.getJsonFromHistory(history, voiceMessages);

        databaseInteractor.update(history.getId(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                Log.d(TAG, "onResult: Saved Voice Messages " + result.getId() + " - " + result.getMessages().toString());
            }
        }, json);

    }

    public void stopListen() {
        long time = getTimeDelay();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                channelInteractor.stopListen();
                updateView(ChannelView.UIState.READY);
            }
        }, time);
    }


    private long getTimeDelay() {
        if (bcStartTime == null)
            return 0;
        long timeDelay;
        if (streamStartTime == null)
            streamStartTime = new Date();
        timeDelay = streamStartTime.getTime() - bcStartTime.getTime();

        streamStartTime = null;
        bcStartTime = null;
        return timeDelay;
    }

    @Override
    public void notify(ChannelInteractor.ChannelInteractorListener.STATE state) {
        switch (state) {
            case STARTED:
                if (isBroadcasting()) {
                    if (activeChannel.getLiveStream().getStreamName().equals(currentChat.getRoom().getLocalpart().toString())) {
                        openFireServer.sendNotification(currentChat, activeChannel.getLiveStream().getStreamName(), activeChannel.getLiveStream().getId());

                    } else {
                        /**
                         * TODO check if it is the actual channel
                         */
                    }
                    showToast("STREAMING");
                }
                streamStartTime = new Date();
                break;
            case AUDIO_MUTE:
                updateView(ChannelView.UIState.READY);
                break;
            case AUDIO_UNMUTE:
                updateView(ChannelView.UIState.RECEIVING);
                if (channelInteractor.isStreaming())
                    activeChannel.getLiveStream().setId(getCounter());
                startListen(activeChannel.getLiveStream());
                streamStartTime = new Date();
                break;
            case MEDIA_NOT_FOUND:
                /**
                 * TODO notify the stream is not active


                 VoiceMessage voiceMessage = red5ProHistory.hasMessage(channel.getLiveStream().getPublishStreamName());
                 if (voiceMessage != null){
                 Log.d(TAG, "notifyMessage: " + channel.getLiveStream().getPublishStreamName());
                 if (!voiceMessage.isEmpty()) {
                 if (playList == null)
                 playList = new Playlist();
                 playList.addMessage(voiceMessage);

                 } else { // Check if it is a live message

                 }
                 } else {
                 /**
                 * Message is not related with the files on the Server.
                 * Cases:
                 * - Old Message
                 * - ChannelEntity does not exist anymore
                 *
                 * TODO Remove this old message from the server

                 }*/
                break;
            case CLOSED:
                if (isPlaying() || isBroadcasting())
                    stopListen();
                break;
            case AUDIO_STARTED_LISTEN:
                streamStartTime = new Date();
                break;
            case STOPPED:
                updateView(ChannelView.UIState.READY);
                break;
        }
    }

    public void setCurrentChat(MultiUserChat currentChat) {
        this.currentChat = currentChat;
    }
}