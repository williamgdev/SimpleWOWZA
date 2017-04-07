package com.randmcnally.bb.poc.dto.red5pro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordedFileResponse extends BaseResponse {
    public List<RecordedFileData> getData() {
        return data;
    }

    public void setData(List<RecordedFileData> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private List<RecordedFileData> data = null;
}
