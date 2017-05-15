package com.randmcnally.bb.poc.model;

import android.support.annotation.NonNull;

import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.util.ChannelUtil;

import org.jivesoftware.smack.packet.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VoiceMessage implements Serializable {
    String name;

    private float fileSize;

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public VoiceMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return fileSize == 0;
    }

    public static VoiceMessage create(RecordedFileData file) {
        VoiceMessage voiceMessage = new VoiceMessage(file.getName().replace(".flv", ""));
        voiceMessage.setFileSize(file.getFileSize());
        return voiceMessage;
    }

    public static VoiceMessage create(Message msg) {
        VoiceMessage voiceMessage = new VoiceMessage(getNameFromMessage(msg));
        return voiceMessage;
    }

    public static VoiceMessage create(String streamName, String streamId) {
        VoiceMessage voiceMessage = new VoiceMessage(ChannelUtil.getPublishName(streamName, streamId));
        return voiceMessage;
    }


    @NonNull
    private static String getNameFromMessage(Message msg) {
        return ChannelUtil.getPublishName(msg.getSubject(), msg.getBody());
    }

    public static List<VoiceMessage> createFromMessages(List<Message> messages) {
        List<VoiceMessage> voiceMessages = new ArrayList<>();
        for (Message msg :
                messages) {
            voiceMessages.add(new VoiceMessage(getNameFromMessage(msg)));
        }
        return voiceMessages;
    }

    public static List<VoiceMessage> createFromVoiceMessagelEntity(List<VoiceMessageEntity> voiceMessageEntityList) {
        if (voiceMessageEntityList == null || voiceMessageEntityList.size() == 0){
            return new ArrayList<>();
        }
        List<VoiceMessage> voiceMessages = new ArrayList<>();
        for (VoiceMessageEntity voiceMessageEntity :
                voiceMessageEntityList) {
            VoiceMessage voiceMessage = new VoiceMessage(voiceMessageEntity.getName());
            voiceMessage.setFileSize(voiceMessageEntity.getFileSize());
            voiceMessages.add(voiceMessage);
        }
        return voiceMessages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof VoiceMessage) {
            VoiceMessage that = (VoiceMessage)obj;
            return (this.getName().equals(that.getName())  && this.getFileSize() == that.getFileSize());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
