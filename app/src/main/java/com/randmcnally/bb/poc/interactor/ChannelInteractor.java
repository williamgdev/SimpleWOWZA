package com.randmcnally.bb.poc.interactor;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.widget.Toast;

import com.randmcnally.bb.poc.activity.ChannelActivity;
import com.randmcnally.bb.poc.callback.StatusLiveStreamCallback;
import com.randmcnally.bb.poc.network.ServiceFactory;
import com.randmcnally.bb.poc.restservice.ApiService;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Microphone;

public class ChannelInteractor{

    private static final String IP_ADDRESS = "192.168.43.212";
    private static final int STREAM_PORT = 8554;
    private static final int API_PORT = 5080;
    private static final String APP_NAME = "live";
    private static final String LICENSE_KEY = "2WDZ-GOA3-XZJJ-YFZE";
    private static final String APP_ID = "com.randmcnally.bb.poc";
    private static final String ACCESS_TOKEN = "123";
    private final ApiService apiService;
    private R5Configuration configuration;
    private R5Stream stream;
    private R5Connection connection;

    private String streamName, channelName;
    private boolean isBroadcasting, isListening;
    private boolean isCheckingStream;

    private InteractorListener interatorListener;

    public ChannelInteractor(String streamName, String channelName) {
        this.streamName = streamName;
        this.channelName = channelName;

        //    http://localhost:5080/api/v1/applications/live/streams/rand_mcnally?accessToken=123

        configuration = new R5Configuration(R5StreamProtocol.RTSP, IP_ADDRESS,  STREAM_PORT, APP_NAME, 1.0f);
        configuration.setLicenseKey(LICENSE_KEY);
        configuration.setBundleID(APP_ID);
        connection = new R5Connection(configuration);
        stream = new R5Stream(connection);


        apiService = ServiceFactory.createAPiService(getBaseUrlAPI());

    }

    public void setR5ConnectionListener(R5ConnectionListener listener){
        stream.setListener(listener);
    }

    public void setInteratorListener(InteractorListener interatorListener){
        this.interatorListener = interatorListener;
    }

    /**
     *
     * @return name of the file created on the server
     */
    public String startBroadcast() {
        if (isListening)
            stop();
        isBroadcasting = true;
        isCheckingStream = false;

        R5Microphone r5Microphone = new R5Microphone();
        stream.attachMic(r5Microphone);

        stream.publish(streamName, R5Stream.RecordType.Record);

        return streamName;
    }

    public void stop() {
        if (stream != null) {
            stream.stop();
        }
        isBroadcasting = false;
        isListening = false;
    }

    public void play() {
        isListening = true;
        stream.play(streamName);
    }

    public boolean isBroadcasting() {
        return isBroadcasting;
    }

    public boolean isListening() {
        return isListening;
    }

    public void checkStream(final StatusLiveStreamCallback liveStreamCallback) {
        isCheckingStream = true;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apiService.getLiveStreamStatistics(APP_NAME, streamName, ACCESS_TOKEN).enqueue(liveStreamCallback);
            }
        }, 500);
    }

    public static String getURLStream(String streamName){
        return "http://" + IP_ADDRESS + ":" + STREAM_PORT + "/api/v1/applications/live/streams/" + streamName + "?accessToken=" + ACCESS_TOKEN;
    }

    public static String getBaseUrlAPI(){
        return "http://" + IP_ADDRESS + ":" + API_PORT + "/api/v1/";
    }


    public void stopListen() {
        if (isListening)
            stop();
    }

    public boolean isCheckingStream() {
        return isCheckingStream;
    }

    public void stopCheckStream() {
        isCheckingStream = false;
    }

    public interface InteractorListener{
        enum STATE {STREAM_CONNECTED, STREAM_DISCONNECTED}
        void notifyInteractorState(STATE state);
    }
}
