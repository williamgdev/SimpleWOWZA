package com.randmcnally.bb.wowza.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("meta")
    @Expose
    private BaseDto meta;

    public BaseDto getMeta() {
        return meta;
    }

    public void setMeta(BaseDto meta) {
        this.meta = meta;
    }
}
