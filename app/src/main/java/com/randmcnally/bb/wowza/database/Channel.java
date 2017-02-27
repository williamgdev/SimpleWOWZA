package com.randmcnally.bb.wowza.database;

import com.randmcnally.bb.wowza.dto.AllStreamsResponse;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import retrofit2.Call;

@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Channel {

    @Id
    private Long id;

    @NotNull
    private String name;

    @Generated(hash = 831271595)
    public Channel(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 459652974)
    public Channel() {
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

}
