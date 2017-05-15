package com.randmcnally.bb.poc.interactor;

import android.util.Log;

import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.util.BroadcasterStream;
import com.randmcnally.bb.poc.util.ReceiverStream;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

public class ChannelInteractor implements R5ConnectionListener {

    private static final String TAG = "ChannelInteractor ->";
    private static ChannelInteractor instance;
    private final ChannelInteractorListener listener;
    private final String ipAddress;
    private BroadcasterStream broadcasterStream;
    private ReceiverStream receiverStream;

    private boolean isBroadcasting, isListening;
    private boolean isStreaming;

    private Channel channel;

    private ChannelInteractor(Channel channel, ChannelInteractorListener listener, String ipAddress) {
        this.listener = listener;
        this.channel = channel;
        this.ipAddress = ipAddress;

        broadcasterStream = new BroadcasterStream(this, ipAddress);
        receiverStream = new ReceiverStream(this, ipAddress);
    }

    public static ChannelInteractor getInstance(Channel channel, ChannelInteractorListener listener, String ipAddress) {
        if (instance == null || !instance.channel.equals(channel)) {
            instance = new ChannelInteractor(channel, listener, ipAddress);
        }
        return instance;
    }

    /**
     * @param id
     * @return name of the file created on the server
     */
    public void startBroadcast(int id) {
        if (broadcasterStream == null)
            broadcasterStream = new BroadcasterStream(this, ipAddress);

        if (isListening)
            stop();
        isBroadcasting = true;
        channel.getLiveStream().setId(id);
        broadcasterStream.startBroadcast(channel.getLiveStream().getPublishStreamName());
    }

    public void stop() {
        if (isBroadcasting) {
            broadcasterStream.stopBroadcast();
            isBroadcasting = false;
            broadcasterStream = null; //Testing if it is work
        } else if (isListening) {
            receiverStream.stop();
            isListening = false;
            receiverStream = null;
        }

    }

    public void play(LiveStream liveStream) {
        isStreaming = true;
        this.channel.setLiveStream(liveStream);

        if (receiverStream == null)
            receiverStream = new ReceiverStream(this, ipAddress);
        if (!isListening) {
            isListening = true;
            receiverStream.play(liveStream.getPublishStreamName());
        }
    }

    public void stopListen() {
        if (isListening)
            stop();
    }

    public boolean isBroadcasting() {
        return isBroadcasting;
    }

    public boolean isListening() {
        return isListening;
    }

    public String getStreamName() {
        return channel.getLiveStream().getStreamName();
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
//        showToast(r5ConnectionEvent.name());
        switch (r5ConnectionEvent) {
            case DISCONNECTED:
                if (isStreaming) {
                    listener.notify(ChannelInteractorListener.STATE.STOPPED);
                    isStreaming = false;
                }
                break;
            case START_STREAMING:
                listener.notify(ChannelInteractorListener.STATE.STARTED);
                break;
            case NET_STATUS:
                switch (r5ConnectionEvent.message) {
                    case "NetStream.Play.UnpublishNotify":
                        stopListen();
                        listener.notify(ChannelInteractorListener.STATE.AUDIO_MUTE);
                        break;
                    case "NetStream.Play.PublishNotify":
                        listener.notify(ChannelInteractorListener.STATE.AUDIO_UNMUTE);

                        break;
                    default:
                        Log.d(TAG, "onConnectionEvent: " + r5ConnectionEvent.name());
                        break;
                }
                break;
            case ERROR:
                stop();
                switch (r5ConnectionEvent.message) {
                    case "No Valid Media Found":
                        listener.notify(ChannelInteractorListener.STATE.MEDIA_NOT_FOUND);
                        break;
                    default:
                        Log.d(TAG, "onConnectionEvent: " + r5ConnectionEvent.name() + " - " + r5ConnectionEvent.message);
                }
            case CLOSE:
                listener.notify(ChannelInteractorListener.STATE.CLOSED);
                break;
            case VIDEO_UNMUTE:
            case CONNECTED:
//                if (!isBroadcasting())
//                    bcStartTime = new Date();
            case AUDIO_UNMUTE:
                listener.notify(ChannelInteractorListener.STATE.AUDIO_STARTED_LISTEN);
                break;
            case LICENSE_VALID:
                break;
            case TIMEOUT:
            case STOP_STREAMING:
            case AUDIO_MUTE:
            case VIDEO_MUTE:
            case LICENSE_ERROR:
            default:
                Log.d(TAG, "onConnectionEvent: " + r5ConnectionEvent.name());
                break;
        }
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public interface ChannelInteractorListener {
        enum STATE {STARTED, AUDIO_MUTE, AUDIO_UNMUTE, MEDIA_NOT_FOUND, CLOSED, AUDIO_STARTED_LISTEN, STOPPED}

        void notify(STATE state);
    }
}
