
package com.randmcnally.bb.wowza.dto;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllStreamsResponse extends BaseResponse{

    @SerializedName("live_streams")
    @Expose
    private List<LiveStream> liveStreams = null;
    @SerializedName("links")
    @Expose
    private List<Link> links = null;

    public List<LiveStream> getLiveStreams() {
        return liveStreams;
    }

    public void setLiveStreams(List<LiveStream> liveStreams) {
        this.liveStreams = liveStreams;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
