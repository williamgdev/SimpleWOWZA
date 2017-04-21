package com.randmcnally.bb.poc.model;

import java.io.Serializable;

public class GroupChat implements Serializable {
    /**
     * this is related with OpenFireServer and Messages
     */
    private String roomId;

    public GroupChat(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

}
