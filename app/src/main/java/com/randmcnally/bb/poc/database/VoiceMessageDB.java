package com.randmcnally.bb.poc.database;

import com.randmcnally.bb.poc.model.VoiceMessage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class VoiceMessageDB {
    @NotNull
    @Property(nameInDb = "FILENAME")
    String name;
    
    @Transient
    private float fileSize;

    @Generated(hash = 1115860857)
    public VoiceMessageDB(@NotNull String name) {
        this.name = name;
    }

    @Generated(hash = 1106236341)
    public VoiceMessageDB() {
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public static VoiceMessageDB create(VoiceMessage voiceMessage) {
        VoiceMessageDB voiceMessageDB = new VoiceMessageDB();
        voiceMessageDB.setName(voiceMessage.getName());
        voiceMessageDB.setFileSize(voiceMessage.getFileSize());
        return voiceMessageDB;
    }
    
}
