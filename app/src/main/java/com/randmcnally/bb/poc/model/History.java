package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;

import org.jivesoftware.smack.packet.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {
    private List<VoiceMessage> history;
    Playlist missedMessages;

    public History() {
        this.history = new ArrayList<>();
    }

    public void setHistory(List<VoiceMessage> history) {
        this.history = history;
    }

    public List<VoiceMessage> getVoiceMessages() {
        return history;
    }

    public Playlist getMissedMessages() {
        return missedMessages;
    }

    public void setMissedMessages(Playlist missedMessages) {
        this.missedMessages = missedMessages;
    }

    public VoiceMessage hasMessage(String s) {
        for (VoiceMessage msg :
                history) {
            if (msg.getName().equals(s)){
                return msg;
            }
        }
        return null;
    }

    /**
     *
     * @param recordedFiles
     * @param extensionFile should be like this example ".mp3". Note it included the dot (.)
     * @return
     */
    public static History create(List<RecordedFileData> recordedFiles, String extensionFile) {
        History history = new History();
        for (RecordedFileData file :
                recordedFiles) {
            if (file.getName().contains(extensionFile)) { //only save the .flv files
                history.getVoiceMessages().add(VoiceMessage.create(file));
            }
        }
        return history;
    }

    public static History create(List<Message> messages) {
        History history = new History();
        history.setHistory(VoiceMessage.createFromMessages(messages));
        return history;
    }
}
