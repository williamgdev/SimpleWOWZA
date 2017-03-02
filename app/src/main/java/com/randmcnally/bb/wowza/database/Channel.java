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

    private String rtspUrl;

    private String streamName;

    private String playBackUrl;

    private String primaryServer;

    private String code;

    private int hostPort;

    private String appName;


    public Channel() {
    }

    @Generated(hash = 909456673)
    public Channel(Long id, @NotNull String name, String rtspUrl, String streamName,
            String playBackUrl, String primaryServer, String code, int hostPort,
            String appName) {
        this.id = id;
        this.name = name;
        this.rtspUrl = rtspUrl;
        this.streamName = streamName;
        this.playBackUrl = playBackUrl;
        this.primaryServer = primaryServer;
        this.code = code;
        this.hostPort = hostPort;
        this.appName = appName;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getPlayBackUrl() {
        return playBackUrl;
    }

    public void setPlayBackUrl(String playBackUrl) {
        this.playBackUrl = playBackUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrimaryServer() {
        return primaryServer;
    }

    public void setPrimaryServer(String primaryServer) {
        this.primaryServer = primaryServer;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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
