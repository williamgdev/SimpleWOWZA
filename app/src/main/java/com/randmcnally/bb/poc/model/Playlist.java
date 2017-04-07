package com.randmcnally.bb.poc.model;

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
}
