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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.HistoryEntityDao;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.util.ChannelUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelView;

import org.jivesoftware.smack.packet.Message;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;



public class ChannelPresenterImpl implements ChannelPresenter, OpenFireServer.OpenFireMessageListener, ChannelInteractor.ChannelInteractorListener{
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
    private Playlist playList;
    private Channel channel;
    private OpenFireServer openFireServer;


    public ChannelPresenterImpl(Channel channel) {
        /**
         * TODO Get the id from the last stream created
         */
        this.channel = channel;
        this.channel.setLiveStream(new LiveStream(channel.getRoomId(), 0)); //the stream name is created with the same name as a roomId
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
            String online = "";

            if (intent.getStringExtra("online") != null) {
                online = intent.getStringExtra("online");
            }
            if (online.equals("true")) {
                if (intent.getStringExtra("stream_name") != null && intent.getStringExtra("stream_id") != null) {
                    channel.setLiveStream(new LiveStream(intent.getStringExtra("stream_name"), Integer.parseInt(intent.getStringExtra("stream_id"))));
                    startListen(channel.getLiveStream());
                }
                else {
                    Toast.makeText(context, "Error: No stream name received", Toast.LENGTH_SHORT).show();
                }
            }
            else {
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
    public void setOpenFireServer(OpenFireServer openFireServer) {
        this.openFireServer = openFireServer;
            openFireServer.joinToChannel(channel.getRoomId(), new OpenFireServer.AuthenticatedGroupChatListener() {
                @Override
                public void onSuccess(List<Message> history) {
                    updateView(ChannelView.UIState.MISSED_MESSAGE);

                }

                @Override
                public void onError(String message) {
                    showToast("No missed messages");
                }
            });

        openFireServer.setMessageListener(this);

    }

    @Override
    public int getMissedMessages() {
        return channel.getHistory().getMissedMessages().size();
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
            if (playList != null) {
                try {
                    BBPlayer bbPlayer = new BBPlayer(playList, new BBPlayer.ListenerBBPlayer() {
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
                                case MESSAGE_COMPLETE:
                                    Log.d(TAG, "onListener: MESSAGE_COMPLETE");
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
//                sendNotification(false);
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

    private void showToast(final String message){
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
        this.channel.setLiveStream(liveStream);
        bcStartTime = new Date();
         if (channelInteractor.isBroadcasting()){
            channelInteractor.stop();
        }
        if(!isPlaying()){
            channelInteractor.play(liveStream);
            updateView(ChannelView.UIState.RECEIVING);
        }
    }

    private int nextCounter() {
        counter ++;
        return getCounter();
    }
    public int getCounter() {
        if (counter > 5)
            counter = 0;
        return counter;
    }

//    public void getMissedMessages(String name, List<Message> history) {
//        final History historyOF = new History();
//        historyOF.setHistory(VoiceMessage.createFromMessages(history));
//        databaseInteractor.readByName(name, new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
//            @Override
//            public void onResult(HistoryEntity result) {
//                List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(result);
//                List<VoiceMessage> missedMessages = ChannelUtil.getMissedMessage(historyOF.getVoiceMessages(), VoiceMessage.createFromVoiceMessagelEntity(voiceMessages));
//                showToast(String.valueOf(missedMessages.size()));
//
//            }
//        });
//    }
//
//    private void getListenedMessages(final History historyOF, String name) {
//        databaseInteractor.readByName(name, new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
//            @Override
//            public void onResult(HistoryEntity result) {
//                List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(result);
//                List<VoiceMessage> missedMessages = ChannelUtil.getMissedMessage(historyOF.getVoiceMessages(), VoiceMessage.createFromVoiceMessagelEntity(voiceMessages));
//                showToast(String.valueOf(missedMessages.size()));
//
//            }
//        });
//    }


//    public void updateHistory(HistoryEntity history, VoiceMessageEntity voiceMessage) {
//        List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(history);
//        voiceMessages.add(voiceMessage);
//        /**
//         * Todo get limit of message globally. Actual is 5.
//         */
//        if (voiceMessages.size() > 5) {
//            voiceMessages.remove(0);
//            // if limit reached, remove the first one
//            // because the first one will be the oldest.
//        }
//
//        Type listType = new TypeToken<List<VoiceMessageEntity>>() {
//        }.getType();
//        Gson gson = new Gson();
//        String json = gson.toJson(voiceMessages, listType);
//
//        databaseInteractor.update(history.getId(), new DatabaseInteractor.DatabaseListener<HistoryEntityDao>() {
//            @Override
//            public void onResult(HistoryEntityDao result) {
//                Log.d(TAG, "onResult: Saved Voice Messages");
//            }
//        }, json);
//
//    }

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
    public void notifyMessage(final String streamName, final String streamId) {
        if (!isBroadcasting()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /**
                     * TODO
                     * 1- Check if the channel is the active and the stream name is related with the channel name
                     * 2- remove the messages depends of the limit declared in the stream. actual is 5
                     * 3- check all channels and notify the user the message for each one
                     * 4- create a state to know when the message is already listened. No start listen the user message
                     * 5- check the file already exists
                     */
                    channel.setLiveStream(new LiveStream(streamName, Integer.parseInt(streamId)));
                    startListen(channel.getLiveStream());
                    Log.d(TAG, "notifyMessage: " + channel.getLiveStream().getPublishStreamName());
                }
            });
        }


    }

    @Override
    public void notify(ChannelInteractor.ChannelInteractorListener.STATE state) {
        switch (state) {
            case STARTED:
                if (isBroadcasting()) {
                    openFireServer.sendNotification(channel.getLiveStream().getStreamName(), channel.getLiveStream().getId());
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
                    channel.getLiveStream().setId(getCounter());
                    startListen(channel.getLiveStream());
                streamStartTime = new Date();
                break;
            case MEDIA_NOT_FOUND:
                /**
                 * TODO notify the stream is not active
                 */

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
                     */
                }
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
}