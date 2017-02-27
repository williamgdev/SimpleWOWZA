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
            temp.setName(liveStream.getName());
            channels.add(temp);
        }
        return channels;
    }
}
