package com.randmcnally.bb.poc.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class PushyAPI {
    public static ObjectMapper mapper = new ObjectMapper();

//
//    public static void sendPush(PushyPushRequest push) {
//
//        HttpClient client = new DefaultHttpClient();
//
//        // Create POST request
//        HttpPost request = new HttpPost("https://api.pushy.me/push?api_key=" + SECRET_API_KEY);
//
//        // Set content type to JSON
//        request.addHeader("Content-Type", "application/json");
//
//        // Convert post data to JSON
//        String json = mapper.writeValueAsString(req);
//
//        // Send post data as string
//        request.setEntity(new StringEntity(json));
//
//        // Execute the request
//        HttpResponse response = client.execute(request, new BasicHttpContext());
//
//        // Get response JSON as string
//        String responseJSON = EntityUtils.toString(response.getEntity());
//
//        // Convert JSON response into HashMap
//        Map<String, Object> map = mapper.readValue(responseJSON, Map.class);
//
//        // Got an error?
//        if (map.containsKey("error")) {
//            // Throw it
//            throw new Exception(map.get("error").toString());
//        }
//    }

    /**
     * Check all Post Parameters here:
     * https://pushy.me/docs/api/send-notifications
     */
    public static class PushyPushRequest {
        public Object data;
        public String[] tokens;
        public int time_to_live;

        public PushyPushRequest(Object data, String[] deviceTokens) {
            this.data = data;
            this.tokens = deviceTokens;
            time_to_live = 1;
        }
    }
}
