package com.randmcnally.bb.poc.model;

import android.support.annotation.NonNull;

import com.randmcnally.bb.poc.database.VoiceMailDB;
import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;
import com.randmcnally.bb.poc.network.Red5ProApiManager;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

public class VoiceMail {
    String name;

    private float fileSize;

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public VoiceMail(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return Red5ProApiManager.getURLStream(name);
    }

    public boolean isEmpty() {
        return fileSize == 0;
    }

    public static VoiceMail create(RecordedFileData file) {
        VoiceMail voiceMail = new VoiceMail(file.getName().replace(".flv", ""));
        voiceMail.setFileSize(file.getFileSize());
        return voiceMail;
    }

    public static VoiceMail create(Message msg) {
        VoiceMail voiceMail = new VoiceMail(getNameFromMessage(msg));
        return voiceMail;
    }

    @NonNull
    private static String getNameFromMessage(Message msg) {
        return msg.getSubject() + "_" + msg.getBody();
    }

    public static List<VoiceMail> createFromMessages(List<Message> messages) {
        List<VoiceMail> voiceMails = new ArrayList<>();
        for (Message msg :
                messages) {
            voiceMails.add(new VoiceMail(getNameFromMessage(msg)));
        }
        return voiceMails;
    }

    public static List<VoiceMail> createFromVoiceMailDB(List<VoiceMailDB> voiceMailDBList) {
        List<VoiceMail> voiceMails = new ArrayList<>();
        for (VoiceMailDB voiceMailDB :
                voiceMailDBList) {
            VoiceMail voiceMail = new VoiceMail(voiceMailDB.getName());
            voiceMail.setFileSize(voiceMailDB.getFileSize());
            voiceMails.add(voiceMail);
        }
        return voiceMails;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof VoiceMail) {
            VoiceMail that = (VoiceMail)obj;
            return (this.getName().equals(that.getName())  && this.getFileSize() == that.getFileSize());
        }
        return false;
    }
}
