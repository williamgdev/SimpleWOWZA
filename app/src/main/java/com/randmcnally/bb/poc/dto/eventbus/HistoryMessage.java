package com.randmcnally.bb.poc.dto.eventbus;

import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.state.MessageState;

public class HistoryMessage {
    private MessageState state;
    private VoiceMessage voicemessage;
    private int position;
    private String duration;
    private int remainingSeconds;
    private double timeMilliseconds;

    public HistoryMessage(VoiceMessage voiceMessage, int position) {
        this.voicemessage = voiceMessage;
        this.position = position;
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

    public double getTimeMilliseconds() {
        return timeMilliseconds;
    }

    public void setTimeMilliseconds(double timeMilliseconds) {
        this.timeMilliseconds = timeMilliseconds;
    }

    public void action() {
        state.performAction(this);
    }
}
