package com.randmcnally.bb.poc.interactor;

import android.os.Handler;
import android.os.Looper;

import com.randmcnally.bb.poc.network.Red5ProApiManager;
import com.randmcnally.bb.poc.util.BroadcasterStream;
import com.randmcnally.bb.poc.util.ReceiverStream;
import com.red5pro.streaming.event.R5ConnectionListener;

public class ChannelInteractor{

    private Red5ProApiManager apiService;

    private BroadcasterStream broadcasterStream;
    private ReceiverStream receiverStream;

    private String _streamName, publishStreamName, channelName;
    private boolean isBroadcasting, isListening;
    private boolean isCheckingStream;

    private InteractorListener interatorListener;
    private boolean isMute;
    private R5ConnectionListener listener;
    private String receiverStreamName;
    private int counter;

    public ChannelInteractor(String streamName, String channelName, R5ConnectionListener listener) {
        this._streamName = streamName;
        publishStreamName = getPublishStreamName(streamName);
        this.channelName = channelName;
        this.listener = listener;

        broadcasterStream = new BroadcasterStream(listener);
        receiverStream = new ReceiverStream(listener);

        apiService = Red5ProApiManager.getInstance();

    }

    private String getPublishStreamName(String streamName) {
        return streamName + "_" + nextCounter();
    }

    private int nextCounter() {
        counter ++;
        return getCounter();
    }

    public void setInteratorListener(InteractorListener interatorListener){
        this.interatorListener = interatorListener;
    }

    /**
     *
     * @return name of the file created on the server
     */
    public String startBroadcast() {
        if (broadcasterStream == null)
            broadcasterStream = new BroadcasterStream(listener);

        if (isListening)
            stop();
        isBroadcasting = true;
        isCheckingStream = false;

        broadcasterStream.startBroadcast(getPublishStreamName(_streamName));

        return publishStreamName;
    }

    public void stop() {
        if (isBroadcasting) {
            broadcasterStream.stopBroadcast();
            isBroadcasting = false;
            broadcasterStream = null; //Testing if it is work
        }
        else if(isListening) {
            receiverStream.stop();
            isListening = false;
            receiverStream = null;
        }

    }

    public void play(String receiverStreamName, String stream_id) {
        this.receiverStreamName = receiverStreamName;

        if (receiverStream == null)
            receiverStream = new ReceiverStream(listener);
        if (!isListening || isMute) {
            isListening = true;
            counter = Integer.parseInt(stream_id);
            receiverStream.play(receiverStreamName + "_" + counter);
            isMute = false;
        }
    }

    public void stopListen() {
        if (isListening)
            stop();
        stopCheckStream();
    }

    public boolean isBroadcasting() {
        return isBroadcasting;
    }

    public boolean isListening() {
        return isListening;
    }

    /**
     *
     * @param streamName
     *
     * @deprecated  {will be removed in the next version} </br>
     *              It is used to know if the stream are you working on is live.
     */
    @Deprecated
    public void _checkStream(final String streamName) {
        isCheckingStream = true;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apiService.getLiveStreamStatistics(streamName, new Red5ProApiManager.LiveStreamApiListener() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }
        }, 500);
    }

    @Deprecated
    public boolean isCheckingStream() {
        return isCheckingStream;
    }

    public void stopCheckStream() {
        isCheckingStream = false;
    }

    public void muteAudio() {
        isMute = true;
    }

    public boolean isMute() {
        return isMute;
    }

    public int getCounter() {
        if (counter > 5)
            counter = 0;
        return counter;
    }

    public interface InteractorListener{
        enum STATE {STREAM_CONNECTED, STREAM_DISCONNECTED}
        void notifyInteractorState(STATE state);
    }

    public String getStreamName() {
        return _streamName;
    }
}
