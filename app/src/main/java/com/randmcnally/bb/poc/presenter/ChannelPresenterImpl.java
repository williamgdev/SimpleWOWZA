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

import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.custom.BBPlayer;
import com.randmcnally.bb.poc.database.VoiceMailDB;
import com.randmcnally.bb.poc.interactor.ChannelInteractor;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMail;
import com.randmcnally.bb.poc.network.Red5ProApiManager;
import com.randmcnally.bb.poc.util.FileUtil;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.ChannelView;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class ChannelPresenterImpl implements ChannelPresenter,
        R5ConnectionListener, OpenFireServer.OpenFireServerListener{
    private static final String TAG = "Broadcast ->";

    ChannelView channelView;
    ChannelInteractor channelInteractor;
    DatabaseInteractor databaseInteractor;
    String message;

    private boolean isStreaming;
    static boolean preparing;

    Date streamStartTime;
    Date bcStartTime;
    private String receiverStreamName;
    Red5ProApiManager red5ProApiManager;
    OpenFireServer openFireServer;

    History history;
    private Playlist playList;


    public ChannelPresenterImpl(String streamName, String channelName) {
        this.channelInteractor = new ChannelInteractor(streamName, channelName, this);
        red5ProApiManager = new Red5ProApiManager();
        red5ProApiManager.getRecordedFiles(new Red5ProApiManager.RecordedFileApiListener() {
            @Override
            public void onSuccess(History history) {
                setHistory(history);
            }

            @Override
            public void onError(String s) {

            }
        });

    }

    @Override
    public void setHistory(History history) {
        this.history = history;
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
                    startListen(intent.getStringExtra("stream_name"), intent.getStringExtra("stream_id"));
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
        String uniqueUID = FileUtil.getDeviceUID(channelView.getContext());
        openFireServer = OpenFireServer.getInstance(uniqueUID);
        openFireServer.setListener(this);
        updateView(ChannelActivity.UIState.READY);

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
    public void startBroadcast() {
        bcStartTime = new Date();

        preparing = true;
        if (channelView.getAudioManager().isMicrophoneMute() == false) {
            channelView.setMicrophoneMute(true);
        }
        updateView(ChannelActivity.UIState.BROADCASTING_PREPARING);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preparing) {
                    preparing = false;
                    channelView.playBipSound();
                    channelView.setMicrophoneMute(false);

                    channelInteractor.startBroadcast(); //Send the filename to the Receiver

                    updateView(ChannelActivity.UIState.BROADCASTING);
                }
            }
        }, 500);
    }

    @Override
    public void stopBroadcast() {
        if (preparing) {
            preparing = false;
            updateView(ChannelActivity.UIState.READY);
            if (playList != null) {
                try {
                    BBPlayer bbPlayer = new BBPlayer(playList, new BBPlayer.ListenerBBPlayer() {
                        @Override
                        public void onListener(BBPlayer.BBPLAYER state) {
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
        updateView(ChannelActivity.UIState.BROADCASTING_STOPPING);
        long time = getTimeDelay();

        channelView.setMicrophoneMute(true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                channelInteractor.stop();
                channelView.setMicrophoneMute(false);
                updateView(ChannelActivity.UIState.READY);
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

    void updateView(final ChannelActivity.UIState state) {
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

    public void startListen(String streamName, String stream_id) {
        bcStartTime = new Date();
        this.receiverStreamName = streamName;
        if (channelInteractor.isBroadcasting()){
            channelInteractor.stop();
        }
        if(channelInteractor.isMute() || !isPlaying()){
            isStreaming = true;
            channelInteractor.play(receiverStreamName, stream_id);
            updateView(ChannelActivity.UIState.RECEIVING);
        }
    }


    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
//        showToast(r5ConnectionEvent.name());
        switch (r5ConnectionEvent) {
            case DISCONNECTED:
                if (isStreaming) {
                    updateView(ChannelActivity.UIState.READY);
                    isStreaming = false;
                }
                break;
            case START_STREAMING:
                if (isBroadcasting()) {
                    openFireServer.sendNotification(channelInteractor.getStreamName(), channelInteractor.getCounter());
                    showToast("STREAMING");
                }
                streamStartTime = new Date();

                break;
            case NET_STATUS:
                switch (r5ConnectionEvent.message){
                    case "NetStream.Play.UnpublishNotify":
                        channelInteractor.muteAudio();
                        channelInteractor.stopListen();
                        updateView(ChannelActivity.UIState.READY);
                        break;
                    case "NetStream.Play.PublishNotify":
                        updateView(ChannelActivity.UIState.RECEIVING);
                        if (isStreaming)
                            startListen(receiverStreamName, String.valueOf(channelInteractor.getCounter()));
                            streamStartTime = new Date();

                        break;
                    default:
                        showToast(r5ConnectionEvent.name());
                        break;
                }
                break;
            case ERROR:
                channelInteractor.stop();
                switch (r5ConnectionEvent.message){
                    case "No Valid Media Found":
                        /**
                         * TODO notify the stream is not active
                         */
                        VoiceMail voiceMail = history.hasMessage(channelInteractor.getFullReceivedStreamName());
                        if (voiceMail != null){
                            Log.d(TAG, "notifyMessage: " + channelInteractor.getFullReceivedStreamName());
                            if (!voiceMail.isEmpty()) {
                                if (playList == null)
                                    playList = new Playlist();
                                playList.addMessage(voiceMail);

                            } else { // Check if it is a live message

                            }
                        } else {
                            /**
                             * Message is not related with the files on the Server.
                             * Cases:
                             * - Old Message
                             * - ChannelDB does not exist anymore
                             *
                             * TODO Remove this old message from the server
                             */
                        }

                        break;

                }
            case CLOSE:
                if (isPlaying() || isBroadcasting())
                    stopListen();
                break;
            case VIDEO_UNMUTE:
            case CONNECTED:
//                if (!isBroadcasting())
//                    bcStartTime = new Date();
            case AUDIO_UNMUTE:
                streamStartTime = new Date();
                break;
            case LICENSE_VALID:
                break;
            case TIMEOUT:
            case STOP_STREAMING:
            case AUDIO_MUTE:
            case VIDEO_MUTE:
            case LICENSE_ERROR:
            default:
                showToast(r5ConnectionEvent.name());
                break;
        }
    }

    public void stopListen() {
        long time = getTimeDelay();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                channelInteractor.stopListen();
                updateView(ChannelActivity.UIState.READY);
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
    public void notifyStatusOpenFireServer(STATE state, String message) {
        showToast(state.toString());
        switch (state) {
            case ERROR:
                channelView.showError(message);
                break;
            case CONNECTION_CLOSED:
                break;
            case RECONNECTION_SUCCESS:
                break;
            case RECONNECTION_FAILED:
                break;
            case AUTHENTICATED:
                openFireServer.joinToChannel("randmcnally");
                final History history = new History();
                history.setHistory(VoiceMail.createFromMessages(openFireServer.getOldMessages()));
//                databaseInteractor.create(VoiceMailDB.create(new VoiceMail("randmcnally_3")), new DatabaseInteractor.CreateDatabaseListener() {
//                    @Override
//                    public void onResult(VoiceMailDB voiceMailDB) {
//                        Log.d(TAG, "onResult: Create " + voiceMailDB.getName());
//                    }
//                });
                databaseInteractor.read(new DatabaseInteractor.GetRowDatabaseListener() {
                    @Override
                    public void onResult(List<VoiceMailDB> voiceMailDBList) {
                        List<VoiceMail> missedMessages = history.getMissedMessage(VoiceMail.createFromVoiceMailDB(voiceMailDBList));
                        playList = Playlist.create(missedMessages);

                    }
                });
                break;
            case CONNECTED:

                break;
        }
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

                     * 2- remove the messages depends of the limit declared in the stream. actual is 5
                     * 3- check all channels and notify the user the message for each one
                     * 4- create a state to know when the message is already listened. No start listen the user message
                     * 5- check the file already exists
                     */

                    startListen(streamName, streamId);
                    Log.d(TAG, "notifyMessage: " + channelInteractor.getFullReceivedStreamName());
                }
            });
        }


    }

}