package com.randmcnally.bb.poc.dao;

import com.randmcnally.bb.poc.model.VoiceMessage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class VoiceMessageEntity {
    @NotNull
    @Property(nameInDb = "FILENAME")
    String name;
    
    @Transient
    private float fileSize;

    @Generated(hash = 493670510)
    public VoiceMessageEntity(@NotNull String name) {
        this.name = name;
    }

    @Generated(hash = 39617361)
    public VoiceMessageEntity() {
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

    public static VoiceMessageEntity create(VoiceMessage voiceMessage) {
        VoiceMessageEntity voiceMessageEntity = new VoiceMessageEntity();
        voiceMessageEntity.setName(voiceMessage.getName());
        voiceMessageEntity.setFileSize(voiceMessage.getFileSize());
        return voiceMessageEntity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof VoiceMessageEntity) {
            VoiceMessageEntity that = (VoiceMessageEntity)obj;
            return (this.getName().equals(that.getName())  && this.getFileSize() == that.getFileSize());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
