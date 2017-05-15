package com.randmcnally.bb.poc.custom;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;


public class BBGroupChat {
    private final List<Message> messages;
    private final MultiUserChat chat;

    public BBGroupChat(MultiUserChat chat, List<Message> messages) {
        this.chat = chat;
        this.messages = messages;
    }

    public String getUniqueIdentifier(){
        return chat.getRoom().getLocalpart().toString();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public MultiUserChat getChat() {
        return chat;
    }
}
