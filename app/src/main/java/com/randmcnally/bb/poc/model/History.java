package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.dto.red5pro.Data;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

public class History {
    private List<VoiceMail> history;

    public History() {
        this.history = new ArrayList<>();
    }

    public void setHistory(List<VoiceMail> history) {
        this.history = history;
    }

    public List<VoiceMail> getHistory() {
        return history;
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
                history.getHistory().add(VoiceMail.create(file));
            }
        }
        return history;
    }

    public VoiceMail hasMessage(String s) {
        for (VoiceMail msg :
                history) {
            if (msg.getName().equals(s)){
                return msg;
            }
        }
        return null;
    }

    public List<VoiceMail> getMissedMessage(List<VoiceMail> voiceMails) {
        List<VoiceMail> missedMessages = history.subList(0, history.size());
        missedMessages.removeAll(voiceMails);

        return missedMessages;
    }
}
