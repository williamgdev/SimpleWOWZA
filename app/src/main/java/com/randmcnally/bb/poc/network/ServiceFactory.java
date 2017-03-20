package com.randmcnally.bb.poc.network;

import com.google.gson.JsonObject;
import com.randmcnally.bb.poc.restservice.ApiService;

import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private static final String TAG = "Service Factory ->";


    public static ApiService createAPiService(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        return apiService;

    }

    public static Request getM3UFileRequest(String m3u8Url){
        final Request request = new Request.Builder()
                //.url("http://wowzaprodhd16-lh.akamaihd.net/i/8095ad94_1@440122/master.m3u8")
                .url(m3u8Url)
                .build();
        return request;
    }

    public static JsonObject buildJsonStream(String name){
        JsonObject json = new JsonObject();
        JsonObject liveStream = new JsonObject();
        liveStream.addProperty("aspect_ratio_height", 72);
        liveStream.addProperty("aspect_ratio_width", 128);
        liveStream.addProperty("billing_mode", "pay_as_you_go");
        liveStream.addProperty("broadcast_location", "us_central_iowa");
        liveStream.addProperty("delivery_protocols", "[\"rtmp\", \"rtsp\", \"wowz\"]");
        liveStream.addProperty("disable_authentication", true);
        liveStream.addProperty("encoder", "wowza_gocoder");
        liveStream.addProperty("hosted_page", true);
        liveStream.addProperty("hosted_page_description", "Push to Talk");
        liveStream.addProperty("hosted_page_sharing_icons", true);
        liveStream.addProperty("hosted_page_title", "Push to Talk");
        liveStream.addProperty("name", name);
        liveStream.addProperty("transcoder_type", "transcoded");

        json.add("live_stream",liveStream);
        return json;

        /**
         * Format Json
         * {
         "live_stream": {
         "aspect_ratio_height": 72,
         "aspect_ratio_width": 128,
         "billing_mode": "pay_as_you_go",
         "broadcast_location": "us_central_iowa",
         "closed_caption_type": "none",
         "delivery_method": "push",
         "delivery_protocols": [
         "rtmp",
         "rtsp",
         "wowz"
         ],
         "disable_authentication": true,
         "encoder": "wowza_gocoder",
         "hosted_page": true,
         "hosted_page_description": "Push to Talk",
         "hosted_page_sharing_icons": true,
         "hosted_page_title": "Push to Talk",
         "name": "My Live Stream",
         "transcoder_type": "transcoded"
         }
         }
         */
    }
}
