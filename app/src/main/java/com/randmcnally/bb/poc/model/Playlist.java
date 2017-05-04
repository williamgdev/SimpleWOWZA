package com.randmcnally.bb.poc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

public class Playlist implements Serializable{
    Stack<VoiceMessage> voiceMessages;

    public Playlist() {
        voiceMessages = new Stack<>();
    }

    public VoiceMessage getOlderMessages() {
        return voiceMessages.pop();
    }

    /**
     *
     * @param voiceMessage
     * @return false if the item already exist
     */
    public boolean addMessage(VoiceMessage voiceMessage) {
        if (voiceMessages.contains(voiceMessage)) {
            return false;
        }
        return voiceMessages.push(voiceMessage) != null;

    }

    public boolean isEmpty() {
        return voiceMessages.isEmpty();
    }

//    public static Playlist create(List<Message> messageList) {
//        Playlist playlist = new Playlist();
//        for (Message msg :
//                messageList) {
//            playlist.addMessage(VoiceMessage.create(msg));
//        }
//        return playlist;
//
//    }


    public static Playlist create(List<VoiceMessage> missedMessages) {
        Playlist playlist = new Playlist();
        for (VoiceMessage voiceMessage :
                missedMessages) {
            playlist.addMessage(voiceMessage);
        }
        return playlist;
    }

    public int size() {
        return voiceMessages.size();
    }
}
