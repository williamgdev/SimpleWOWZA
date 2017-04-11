package com.randmcnally.bb.poc.model;

import org.jivesoftware.smack.packet.Message;

import java.util.List;
import java.util.Stack;

public class Playlist {
    Stack<VoiceMail> voiceMails = new Stack<>();

    public VoiceMail getOlderMessages() {
        return voiceMails.pop();
    }

    public void addMessage(VoiceMail voiceMail) {
        voiceMails.push(voiceMail);
    }

    public boolean isEmpty() {
        return voiceMails.isEmpty();
    }

//    public static Playlist create(List<Message> messageList) {
//        Playlist playlist = new Playlist();
//        for (Message msg :
//                messageList) {
//            playlist.addMessage(VoiceMail.create(msg));
//        }
//        return playlist;
//
//    }


    public static Playlist create(List<VoiceMail> missedMessages) {
        Playlist playlist = new Playlist();
        for (VoiceMail voiceMail :
                missedMessages) {
            playlist.addMessage(voiceMail);
        }
        return playlist;
    }
}
