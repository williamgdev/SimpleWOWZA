
package com.randmcnally.bb.wowza.dto.transcorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyframeInterval {

    @SerializedName("value")
    @Expose
    private Integer value;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("units")
    @Expose
    private String units;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

}
