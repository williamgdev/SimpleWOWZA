package com.randmcnally.bb.poc.model;


import com.randmcnally.bb.poc.dao.ChannelEntity;
import com.randmcnally.bb.poc.dto.openfire.ChatRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Channel extends GroupChat implements Serializable{

    private String fullName;
    History history;
    LiveStream liveStream;
    boolean isFavorite;

    public Channel(String channelFullName, String channelName) {
        super(channelName);
        this.fullName = channelFullName;
        history = new History();
        liveStream = new LiveStream(channelName);
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

    public String getFullName() {
        return fullName;
    }

    public static List<Channel> createFromChatRoom(List<ChatRoom> chatRooms) {
        List<Channel> channels = new ArrayList<>();
        for (ChatRoom chatRoom :
                chatRooms) {
            channels.add(Channel.create(chatRoom));
        }
        return channels;

    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public static List<Channel> createFromChannelEntity(List<ChannelEntity> channelEntities) {
        List<Channel> channels = new ArrayList<>();
        for (ChannelEntity channelEntity :
                channelEntities) {
            channels.add(Channel.create(channelEntity));
        }
        return channels;
    }

    private static Channel create(ChannelEntity channelEntity) {
        Channel result = new Channel("", channelEntity.getName());
        result.setFavorite(channelEntity.isFavorite());
        return result; //TODO Fix that in the channel does not exist id
    }

    private static Channel create(ChatRoom chatRoom) {
        Channel channel = new Channel(chatRoom.getNaturalName(), chatRoom.getRoomName());
        return channel;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Channel) {
            Channel that = (Channel)obj;
            return (this.getName().equals(that.getName()));
// TODO  Use all fields
//         return (this.getName().equals(that.getName())  && this.history.equals(that.history) && this.liveStream.equals(that.liveStream));
        }
        return false;
    }

    public static Comparator<? super Channel> getComparator() {
        return new Comparator<Channel>() {
            @Override
            public int compare(Channel o1, Channel o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }
}
