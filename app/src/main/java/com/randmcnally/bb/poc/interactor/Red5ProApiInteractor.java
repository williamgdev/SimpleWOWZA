package com.randmcnally.bb.poc.interactor;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.randmcnally.bb.poc.BBApplication;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.dto.red5pro.LiveStreamResponse;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileResponse;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.restservice.Red5ProApiService;
import com.randmcnally.bb.poc.util.FlvMetadataRetrieve;
import com.randmcnally.bb.poc.view.ChannelView;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Red5ProApiInteractor {
    public static final int STREAM_PORT = 8554;
    static final int API_PORT = 5080;
    public static final String APP_ID = "com.randmcnally.bb.poc";
//
//    public static final String IP_ADDRESS = "192.168.43.212";
//    public static final String SDK_LICENSE_KEY = "X3UH-6RKQ-JKPE-NO3R";
//    AWS Config

    public static final String SDK_LICENSE_KEY = "YZF3-4Y36-3ROB-CSXV";

    public static final String ACCESS_TOKEN = "123";
    public static final String APP_NAME = "live";

    private static Red5ProApiInteractor instance;
    private Red5ProApiService apiService;
    private boolean isCheckingStream;


    private Red5ProApiInteractor(String ipAddress) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrlAPI(ipAddress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(Red5ProApiService.class);

    }

    public static Red5ProApiInteractor getInstance(String ipAddress) {
        if (instance == null) {
            instance = new Red5ProApiInteractor(ipAddress);
        }
        return instance;
    }

    public static String getURLStream(String streamName, String ipAddress){
        //http://192.168.43.212:5080/live/streams/randmcnally_3.flv
        return "http://" + ipAddress + ":" + API_PORT + "/live/streams/" + streamName + ".flv";
    }

    public static String getBaseUrlAPI(String ipAddress){
        return "http://" + ipAddress + ":" + API_PORT + "/api/v1/";
    }

    public void getLiveStreamStatistics(String streamName, LiveStreamApiListener listener){
        apiService.getLiveStreamStatistics(APP_NAME, streamName, ACCESS_TOKEN).enqueue(getLiveStreamCallback(listener));
    }

    public void getRecordedFiles(RecordedFileApiListener listener){
        apiService.getRecordedFiles(APP_NAME, ACCESS_TOKEN).enqueue(getRecordedFilesCallback(listener));
    }

    private Callback<RecordedFileResponse> getRecordedFilesCallback(final RecordedFileApiListener listener) {
        return new Callback<RecordedFileResponse>() {
            @Override
            public void onResponse(Call<RecordedFileResponse> call, Response<RecordedFileResponse> response) {
                switch (response.code()){
                    case RecordedFileApiListener.OK:
                        if (response.body().getData() != null){
                            listener.onSuccess(History.create(response.body().getData(), ".flv"));
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<RecordedFileResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        };
    }

    private Callback<LiveStreamResponse> getLiveStreamCallback(final LiveStreamApiListener listener) {
        return new Callback<LiveStreamResponse>() {
            @Override
            public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                switch (response.code()){

                }
            }

            @Override
            public void onFailure(Call<LiveStreamResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        };
    }

    public static void updateDuration(List<HistoryMessage> historyMessages, String ipAddress) throws Exception {
        for (HistoryMessage historyMessage :
                historyMessages) {
            String url = getURLStream(historyMessage.getVoicemessage().getName(), ipAddress);
            FlvMetadataRetrieve flvMetaData = new FlvMetadataRetrieve(url);
            historyMessage.setDuration(flvMetaData.getDuration());
            historyMessage.setTimeMilliseconds(flvMetaData.getTimeMilliseconds());
        }
    }

    public interface LiveStreamApiListener extends BaseApiListener {
        void onSuccess(String s);
        void onError(String s);
    }

    public interface RecordedFileApiListener extends BaseApiListener{
        void onSuccess(History history);
        void onError(String s);

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
                getLiveStreamStatistics(streamName, new LiveStreamApiListener() {
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



}
