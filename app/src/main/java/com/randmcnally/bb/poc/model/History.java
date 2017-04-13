package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;

import java.util.ArrayList;
import java.util.List;

public class History {
    private List<VoiceMessage> history;

    public History() {
        this.history = new ArrayList<>();
    }

    public void setHistory(List<VoiceMessage> history) {
        this.history = history;
    }

    public List<VoiceMessage> getHistory() {
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
                history.getHistory().add(VoiceMessage.create(file));
            }
        }
        return history;
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

    public List<VoiceMessage> getMissedMessage(List<VoiceMessage> voiceMessages) {
        List<VoiceMessage> missedMessages = history.subList(0, history.size());
        missedMessages.removeAll(voiceMessages);

        return missedMessages;
    }
}
