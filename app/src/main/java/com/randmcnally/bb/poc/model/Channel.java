package com.randmcnally.bb.poc.model;


import com.randmcnally.bb.poc.dto.openfire.ChatRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channel extends GroupChat implements Serializable{

    private String name;
    History history;
    LiveStream liveStream;

    public Channel(String channelName, String roomId) {
        super(roomId);
        this.name = channelName;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public LiveStream getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(LiveStream liveStream) {
        /**
         * TODO Implement list of LiveStreams
         */
        this.liveStream = liveStream;
    }

    public String getName() {
        return name;
    }

    public static List<Channel> create(List<ChatRoom> chatRooms) {
        List<Channel> channels = new ArrayList<>();
        for (ChatRoom chatRoom :
                chatRooms) {
            channels.add(Channel.create(chatRoom));
        }
        return channels;

    }

    private static Channel create(ChatRoom chatRoom) {
        Channel channel = new Channel(chatRoom.getNaturalName(), chatRoom.getRoomName());
        return channel;
    }

    @Override
    public String toString() {
        return getName();
    }
}
