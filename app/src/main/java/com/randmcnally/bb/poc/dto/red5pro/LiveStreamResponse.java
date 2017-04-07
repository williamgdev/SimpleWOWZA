
package com.randmcnally.bb.poc.dto.red5pro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveStreamResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private LiveStreamData data;

    public LiveStreamData getData() {
        return data;
    }

    public void setData(LiveStreamData data) {
        this.data = data;
    }

}
