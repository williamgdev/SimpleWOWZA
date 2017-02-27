
package com.randmcnally.bb.wowza.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusResponse extends BaseResponse{

    @SerializedName("live_stream")
    @Expose
    private LiveStream liveStream;

    public LiveStream getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(LiveStream liveStream) {
        this.liveStream = liveStream;
    }

}
