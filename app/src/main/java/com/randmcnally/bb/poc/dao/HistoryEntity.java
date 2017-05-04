package com.randmcnally.bb.poc.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.List;

@Entity
public class HistoryEntity {
    @Id
    String id;
    @NotNull
    String messages;


    @Generated(hash = 319229753)
    public HistoryEntity(String id, @NotNull String messages) {
        this.id = id;
        this.messages = messages;
    }

    @Keep
    public HistoryEntity() {
        id = "";
        messages = "";
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessages() {
        return this.messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    /**
     *
     * @param id
     * @param histories
     * @return If the name it does not exist the result is -1.
     */
    public static int findItem(String id, List<HistoryEntity> histories) {
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.getId() + "-" + messages;
    }
}
