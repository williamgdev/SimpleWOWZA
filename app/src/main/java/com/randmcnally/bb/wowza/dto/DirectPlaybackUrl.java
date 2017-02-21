
package com.randmcnally.bb.wowza.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectPlaybackUrl extends BaseDto{

    @SerializedName("rtmp")
    @Expose
    private List<Rtmp> rtmp = null;
    @SerializedName("rtsp")
    @Expose
    private List<Rtsp> rtsp = null;
    @SerializedName("wowz")
    @Expose
    private List<Wowz> wowz = null;

    public List<Rtmp> getRtmp() {
        return rtmp;
    }

    public void setRtmp(List<Rtmp> rtmp) {
        this.rtmp = rtmp;
    }

    public List<Rtsp> getRtsp() {
        return rtsp;
    }

    public void setRtsp(List<Rtsp> rtsp) {
        this.rtsp = rtsp;
    }

    public List<Wowz> getWowz() {
        return wowz;
    }

    public void setWowz(List<Wowz> wowz) {
        this.wowz = wowz;
    }

}
