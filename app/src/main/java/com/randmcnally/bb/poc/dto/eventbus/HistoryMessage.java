package com.randmcnally.bb.poc.dto.eventbus;

import android.widget.SeekBar;

import com.randmcnally.bb.poc.model.VoiceMessage;
import com.randmcnally.bb.poc.state.MessageState;

public class HistoryMessage {
    private MessageState state;
    private VoiceMessage voicemessage;
    private int position;
    private double remainingSeconds;

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

    public double getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(double remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public void action(SeekBar seekBar) {
        state.performAction(this, seekBar);
    }
}
