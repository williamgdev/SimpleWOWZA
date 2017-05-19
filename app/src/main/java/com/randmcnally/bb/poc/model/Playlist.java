package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Playlist implements Serializable{
    Queue<VoiceMessage> voiceMessages;

    public Playlist() {
        voiceMessages = new LinkedList<>();
    }

    public VoiceMessage nextMessage() {
        return voiceMessages.remove();
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
        return voiceMessages.add(voiceMessage);

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

    public static Playlist createFromMessage(List<VoiceMessage> voiceMessages, VoiceMessage currentMessage) {
        Playlist result = new Playlist();
        boolean addVoiceMessage = false;
        for (VoiceMessage voiceMessage :
                voiceMessages) {
            if (addVoiceMessage || voiceMessage.equals(currentMessage)){
                addVoiceMessage = true;
                result.addMessage(voiceMessage);
            }
        }
        return result;
    }

    public static Playlist createFromMessage(List<HistoryMessage> history, HistoryMessage currentPlayMessage) {
        Playlist result = new Playlist();
        boolean addVoiceMessage = false;
        for (HistoryMessage historyMessage :
                history) {
            if (addVoiceMessage || historyMessage.getVoicemessage().equals(currentPlayMessage.getVoicemessage())){
                addVoiceMessage = true;
                result.addMessage(historyMessage.getVoicemessage());
            }
        }
        return result;
    }
}
