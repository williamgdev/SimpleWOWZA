
package com.randmcnally.bb.wowza.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randmcnally.bb.wowza.dto.transcorder.Transcoder;

public class TranscorderResponse extends BaseResponse{

    @SerializedName("transcoder")
    @Expose
    private Transcoder transcoder;

    public Transcoder getTranscoder() {
        return transcoder;
    }

    public void setTranscoder(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

}
