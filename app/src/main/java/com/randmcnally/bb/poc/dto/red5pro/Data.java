
package com.randmcnally.bb.poc.dto.red5pro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("bytes_received")
    @Expose
    private Integer bytesReceived;
    @SerializedName("active_subscribers")
    @Expose
    private Integer activeSubscribers;
    @SerializedName("total_subscribers")
    @Expose
    private Integer totalSubscribers;
    @SerializedName("max_subscribers")
    @Expose
    private Integer maxSubscribers;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("publish_name")
    @Expose
    private String publishName;
    @SerializedName("creation_time")
    @Expose
    private Double creationTime;
    @SerializedName("scope_path")
    @Expose
    private String scopePath;
    @SerializedName("is_recording")
    @Expose
    private Boolean isRecording;
    @SerializedName("state")
    @Expose
    private String state;

    public Integer getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(Integer bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public Integer getActiveSubscribers() {
        return activeSubscribers;
    }

    public void setActiveSubscribers(Integer activeSubscribers) {
        this.activeSubscribers = activeSubscribers;
    }

    public Integer getTotalSubscribers() {
        return totalSubscribers;
    }

    public void setTotalSubscribers(Integer totalSubscribers) {
        this.totalSubscribers = totalSubscribers;
    }

    public Integer getMaxSubscribers() {
        return maxSubscribers;
    }

    public void setMaxSubscribers(Integer maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }

    public Double getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Double creationTime) {
        this.creationTime = creationTime;
    }

    public String getScopePath() {
        return scopePath;
    }

    public void setScopePath(String scopePath) {
        this.scopePath = scopePath;
    }

    public Boolean getIsRecording() {
        return isRecording;
    }

    public void setIsRecording(Boolean isRecording) {
        this.isRecording = isRecording;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
