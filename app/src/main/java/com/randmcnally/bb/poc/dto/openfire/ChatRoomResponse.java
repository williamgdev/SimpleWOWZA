package com.randmcnally.bb.poc.dto.openfire;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="chatRooms", strict = false)
public class ChatRoomResponse  extends BaseResponse{
    @ElementList (inline=true)
    private List<ChatRoom> chatRooms;

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRoom(List<ChatRoom> chatRoom) {
        this.chatRooms = chatRoom;
    }

    @Override
    public String toString() {
        return "ClassPojo [chatRoom = " + chatRooms + "]";
    }

}
