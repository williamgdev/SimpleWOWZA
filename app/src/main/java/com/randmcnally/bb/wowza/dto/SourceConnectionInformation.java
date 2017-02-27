
package com.randmcnally.bb.wowza.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SourceConnectionInformation extends BaseDto{

    @SerializedName("primary_server")
    @Expose
    private String primaryServer;
    @SerializedName("host_port")
    @Expose
    private Integer hostPort;
    @SerializedName("application")
    @Expose
    private String application;
    @SerializedName("stream_name")
    @Expose
    private String streamName;
    @SerializedName("disable_authentication")
    @Expose
    private Boolean disableAuthentication;
    @SerializedName("username")
    @Expose
    private Object username;
    @SerializedName("password")
    @Expose
    private Object password;

    public String getPrimaryServer() {
        return primaryServer;
    }

    public void setPrimaryServer(String primaryServer) {
        this.primaryServer = primaryServer;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public Boolean getDisableAuthentication() {
        return disableAuthentication;
    }

    public void setDisableAuthentication(Boolean disableAuthentication) {
        this.disableAuthentication = disableAuthentication;
    }

    public Object getUsername() {
        return username;
    }

    public void setUsername(Object username) {
        this.username = username;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

}
