package com.randmcnally.bb.poc.network;

import android.util.Log;

import com.randmcnally.bb.poc.dto.red5pro.LiveStreamResponse;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileResponse;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.restservice.Red5ProApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Red5ProApiManager {
    public static final String IP_ADDRESS = "192.168.43.212";
    //  private static final String IP_ADDRESS = "192.168.1.233";
    public static final int STREAM_PORT = 8554;
    static final int API_PORT = 5080;
    public static final String SDK_LICENSE_KEY = "2WDZ-GOA3-XZJJ-YFZE";
    public static final String APP_ID = "com.randmcnally.bb.poc";

    public static final String ACCESS_TOKEN = "123";
    public static final String APP_NAME = "live";

    private static Red5ProApiManager instance;
    private Red5ProApiService apiService;

    public Red5ProApiManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrlAPI())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(Red5ProApiService.class);

    }

    public static Red5ProApiManager getInstance() {
        if (instance == null) {
            instance = new Red5ProApiManager();
        }
        return instance;
    }

    public static String getURLStream(String streamName){
        //http://192.168.43.212:5080/live/streams/randmcnally_3.flv
        return "http://" + IP_ADDRESS + ":" + API_PORT + "/live/streams/" + streamName + ".flv";
    }

    public static String getBaseUrlAPI(){
        return "http://" + IP_ADDRESS + ":" + API_PORT + "/api/v1/";
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

    public interface LiveStreamApiListener extends BaseApiListener{
        void onSuccess(String s);
        void onError(String s);
    }

    public interface RecordedFileApiListener extends BaseApiListener{
        void onSuccess(History history);
        void onError(String s);

    }
}
