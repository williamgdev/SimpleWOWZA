package com.randmcnally.bb.wowza.util;

import com.randmcnally.bb.wowza.database.Channel;
import com.randmcnally.bb.wowza.dto.AllStreamsResponse;
import com.randmcnally.bb.wowza.dto.LiveStream;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ChannelUtil {
    public static List<Channel> toChannel(List<LiveStream> allLiveStreams){
        List<Channel> channels = new ArrayList<>();
        for (LiveStream liveStream :
                allLiveStreams) {
            Channel temp = new Channel();
            temp.setAppName(liveStream.getSourceConnectionInformation().getApplication());
            temp.setName(liveStream.getName());
            temp.setStreamName(liveStream.getSourceConnectionInformation().getStreamName());
            temp.setPlayBackUrl(liveStream.getPlayerHlsPlaybackUrl());
            temp.setHostPort(liveStream.getSourceConnectionInformation().getHostPort());
            temp.setPrimaryServer(liveStream.getSourceConnectionInformation().getPrimaryServer());
            temp.setRtspUrl(liveStream.getDirectPlaybackUrls().getRtsp().get(0).getUrl());
            temp.setCode(liveStream.getId());

            channels.add(temp);
        }
        return channels;
    }

//    public static List<Channel> toChannelOnlyStarted(List<LiveStream> allLiveStreams) {
//        List<Channel> channels = new ArrayList<>();
//        for (LiveStream liveStream :
//                allLiveStreams) {
//            if (liveStream.getState() != null) {
//                if (liveStream.getState().equals("started")) {
//                    Channel temp = new Channel();
//                    temp.setName(liveStream.getName());
//                    channels.add(temp);
//                }
//            }
//        }
//        return channels;
//    }
}
