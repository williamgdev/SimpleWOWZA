package com.randmcnally.bb.poc.network;

import com.randmcnally.bb.poc.dto.red5pro.LiveStreamResponse;
import com.randmcnally.bb.poc.restservice.Red5ProApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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

    private Red5ProApiManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrlAPI())
                .client(OpenFireInterceptor.buildHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
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
        return "http://" + IP_ADDRESS + ":" + STREAM_PORT + "/api/v1/applications/live/streams/" + streamName + "?accessToken=" + ACCESS_TOKEN;
    }

    public static String getBaseUrlAPI(){
        return "http://" + IP_ADDRESS + ":" + API_PORT + "/api/v1/";
    }

    public void getLiveStreamStatistics(String streamName, LiveStreamApiListener listener){
        apiService.getLiveStreamStatistics(APP_NAME, streamName, ACCESS_TOKEN).enqueue(getLiveStreamCallback(listener));
    }

    private Callback<LiveStreamResponse> getLiveStreamCallback(final LiveStreamApiListener listener) {
        return new Callback<LiveStreamResponse>() {
            @Override
            public void onResponse(Call<LiveStreamResponse> call, Response<LiveStreamResponse> response) {
                switch (response.code()){
                    case LiveStreamApiListener.OK:
                        listener.onSuccess("The Stream is started.");
                        break;
                    case LiveStreamApiListener.NOT_FOIND:
                        listener.onError("Resource not found.");
                        break;
                    case LiveStreamApiListener.BAD_REQUEST:
                        listener.onError("Bad Request because of incorrect number of arguments, or argument format or the order or arguments.");
                        break;
                    case LiveStreamApiListener.CONFLICT:
                        listener.onError("Invalid operation requested. An operation is requested which cannot be performed given the current state of target resource.");
                        break;
                    case LiveStreamApiListener.SERVER_ERROR:
                        listener.onError("An I/O operation failure occurred.Stream not found in application live.");
                        break;
                    case LiveStreamApiListener.UNAUTHORIZED:
                        listener.onError("Unauthorized. The http client is denied access. Authentication information is missing or invalid.");
                        break;
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
}
