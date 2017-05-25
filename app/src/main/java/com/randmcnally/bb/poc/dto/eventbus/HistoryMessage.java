package com.randmcnally.bb.poc.dto.eventbus;

import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.state.MessageState;

public class HistoryMessage {
    private MessageState state;
    private VoiceMessage voicemessage;
    private int position;
    private String duration;
    private int remainingSeconds;
    private long timeMilliseconds;

    public HistoryMessage(VoiceMessage voiceMessage, int position) {
        this.voicemessage = voiceMessage;
        this.position = position;
        timeMilliseconds = 0;
//        this.state = new MessageStop();
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public VoiceMessage getVoicemessage() {
        return voicemessage;
    }

    public void setVoicemessage(VoiceMessage voicemessage) {
        this.voicemessage = voicemessage;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public long getTimeMilliseconds() {
        return timeMilliseconds;
    }

    public void setTimeMilliseconds(long timeMilliseconds) {
        this.timeMilliseconds = timeMilliseconds;
    }

    public void action() {
        state.performAction(this);
    }
}
