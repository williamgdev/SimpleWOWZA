package com.randmcnally.bb.poc.model;

import java.io.Serializable;

public class GroupChat implements Serializable {
    /**
     * this is related with OpenFireServer and Messages
     */
    private String name;

    public GroupChat(String roomId) {
        this.name = roomId;
    }

    public String getName() {
        return name;
    }

}
