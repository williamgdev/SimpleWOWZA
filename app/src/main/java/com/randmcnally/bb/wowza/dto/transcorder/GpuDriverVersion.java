
package com.randmcnally.bb.wowza.dto.transcorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GpuDriverVersion {

    @SerializedName("value")
    @Expose
    private Object value;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("units")
    @Expose
    private String units;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
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
