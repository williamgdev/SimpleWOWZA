package com.randmcnally.bb.poc.model;

import com.randmcnally.bb.poc.dto.red5pro.RecordedFileData;
import com.randmcnally.bb.poc.network.Red5ProApiManager;

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
}
