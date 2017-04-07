package com.randmcnally.bb.poc.dto.red5pro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordedFileData extends Data{
    @SerializedName("last_modified")
    @Expose
    private double lastModified;
    @SerializedName("file_size")
    @Expose
    private float fileSize;

    public double getLastModified() {
        return lastModified;
    }

    public void setLastModified(double lastModified) {
        this.lastModified = lastModified;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }
}
