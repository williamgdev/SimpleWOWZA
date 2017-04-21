package com.randmcnally.bb.poc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

public class Playlist implements Serializable{
    Stack<VoiceMessage> voiceMessages = new Stack<>();

    public VoiceMessage getOlderMessages() {
        return voiceMessages.pop();
    }

    public void addMessage(VoiceMessage voiceMessage) {
        voiceMessages.push(voiceMessage);
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
