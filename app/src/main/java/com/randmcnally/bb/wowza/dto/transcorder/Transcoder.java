
package com.randmcnally.bb.wowza.dto.transcorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randmcnally.bb.wowza.dto.transcorder.AudioCodec;
import com.randmcnally.bb.wowza.dto.transcorder.BitsInRate;
import com.randmcnally.bb.wowza.dto.transcorder.BitsOutRate;
import com.randmcnally.bb.wowza.dto.transcorder.BytesInRate;
import com.randmcnally.bb.wowza.dto.transcorder.BytesOutRate;
import com.randmcnally.bb.wowza.dto.transcorder.ConfiguredBytesOutRate;
import com.randmcnally.bb.wowza.dto.transcorder.Connected;
import com.randmcnally.bb.wowza.dto.transcorder.Cpu;
import com.randmcnally.bb.wowza.dto.transcorder.FrameRate;
import com.randmcnally.bb.wowza.dto.transcorder.FrameSize;
import com.randmcnally.bb.wowza.dto.transcorder.GpuDecoderUsage;
import com.randmcnally.bb.wowza.dto.transcorder.GpuDriverVersion;
import com.randmcnally.bb.wowza.dto.transcorder.GpuEncoderUsage;
import com.randmcnally.bb.wowza.dto.transcorder.GpuMemoryUsage;
import com.randmcnally.bb.wowza.dto.transcorder.GpuUsage;
import com.randmcnally.bb.wowza.dto.transcorder.Height;
import com.randmcnally.bb.wowza.dto.transcorder.KeyframeInterval;
import com.randmcnally.bb.wowza.dto.transcorder.UniqueViews;
import com.randmcnally.bb.wowza.dto.transcorder.VideoCodec;
import com.randmcnally.bb.wowza.dto.transcorder.Width;

public class Transcoder {

    @SerializedName("unique_views")
    @Expose
    private UniqueViews uniqueViews;
    @SerializedName("connected")
    @Expose
    private Connected connected;
    @SerializedName("cpu")
    @Expose
    private Cpu cpu;
    @SerializedName("bytes_in_rate")
    @Expose
    private BytesInRate bytesInRate;
    @SerializedName("bytes_out_rate")
    @Expose
    private BytesOutRate bytesOutRate;
    @SerializedName("configured_bytes_out_rate")
    @Expose
    private ConfiguredBytesOutRate configuredBytesOutRate;
    @SerializedName("width")
    @Expose
    private Width width;
    @SerializedName("height")
    @Expose
    private Height height;
    @SerializedName("frame_size")
    @Expose
    private FrameSize frameSize;
    @SerializedName("frame_rate")
    @Expose
    private FrameRate frameRate;
    @SerializedName("keyframe_interval")
    @Expose
    private KeyframeInterval keyframeInterval;
    @SerializedName("video_codec")
    @Expose
    private VideoCodec videoCodec;
    @SerializedName("audio_codec")
    @Expose
    private AudioCodec audioCodec;
    @SerializedName("bits_in_rate")
    @Expose
    private BitsInRate bitsInRate;
    @SerializedName("bits_out_rate")
    @Expose
    private BitsOutRate bitsOutRate;
    @SerializedName("gpu_driver_version")
    @Expose
    private GpuDriverVersion gpuDriverVersion;
    @SerializedName("gpu_usage")
    @Expose
    private GpuUsage gpuUsage;
    @SerializedName("gpu_memory_usage")
    @Expose
    private GpuMemoryUsage gpuMemoryUsage;
    @SerializedName("gpu_encoder_usage")
    @Expose
    private GpuEncoderUsage gpuEncoderUsage;
    @SerializedName("gpu_decoder_usage")
    @Expose
    private GpuDecoderUsage gpuDecoderUsage;

    public UniqueViews getUniqueViews() {
        return uniqueViews;
    }

    public void setUniqueViews(UniqueViews uniqueViews) {
        this.uniqueViews = uniqueViews;
    }

    public Connected getConnected() {
        return connected;
    }

    public void setConnected(Connected connected) {
        this.connected = connected;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    public BytesInRate getBytesInRate() {
        return bytesInRate;
    }

    public void setBytesInRate(BytesInRate bytesInRate) {
        this.bytesInRate = bytesInRate;
    }

    public BytesOutRate getBytesOutRate() {
        return bytesOutRate;
    }

    public void setBytesOutRate(BytesOutRate bytesOutRate) {
        this.bytesOutRate = bytesOutRate;
    }

    public ConfiguredBytesOutRate getConfiguredBytesOutRate() {
        return configuredBytesOutRate;
    }

    public void setConfiguredBytesOutRate(ConfiguredBytesOutRate configuredBytesOutRate) {
        this.configuredBytesOutRate = configuredBytesOutRate;
    }

    public Width getWidth() {
        return width;
    }

    public void setWidth(Width width) {
        this.width = width;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(Height height) {
        this.height = height;
    }

    public FrameSize getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(FrameSize frameSize) {
        this.frameSize = frameSize;
    }

    public FrameRate getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(FrameRate frameRate) {
        this.frameRate = frameRate;
    }

    public KeyframeInterval getKeyframeInterval() {
        return keyframeInterval;
    }

    public void setKeyframeInterval(KeyframeInterval keyframeInterval) {
        this.keyframeInterval = keyframeInterval;
    }

    public VideoCodec getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(VideoCodec videoCodec) {
        this.videoCodec = videoCodec;
    }

    public AudioCodec getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(AudioCodec audioCodec) {
        this.audioCodec = audioCodec;
    }

    public BitsInRate getBitsInRate() {
        return bitsInRate;
    }

    public void setBitsInRate(BitsInRate bitsInRate) {
        this.bitsInRate = bitsInRate;
    }

    public BitsOutRate getBitsOutRate() {
        return bitsOutRate;
    }

    public void setBitsOutRate(BitsOutRate bitsOutRate) {
        this.bitsOutRate = bitsOutRate;
    }

    public GpuDriverVersion getGpuDriverVersion() {
        return gpuDriverVersion;
    }

    public void setGpuDriverVersion(GpuDriverVersion gpuDriverVersion) {
        this.gpuDriverVersion = gpuDriverVersion;
    }

    public GpuUsage getGpuUsage() {
        return gpuUsage;
    }

    public void setGpuUsage(GpuUsage gpuUsage) {
        this.gpuUsage = gpuUsage;
    }

    public GpuMemoryUsage getGpuMemoryUsage() {
        return gpuMemoryUsage;
    }

    public void setGpuMemoryUsage(GpuMemoryUsage gpuMemoryUsage) {
        this.gpuMemoryUsage = gpuMemoryUsage;
    }

    public GpuEncoderUsage getGpuEncoderUsage() {
        return gpuEncoderUsage;
    }

    public void setGpuEncoderUsage(GpuEncoderUsage gpuEncoderUsage) {
        this.gpuEncoderUsage = gpuEncoderUsage;
    }

    public GpuDecoderUsage getGpuDecoderUsage() {
        return gpuDecoderUsage;
    }

    public void setGpuDecoderUsage(GpuDecoderUsage gpuDecoderUsage) {
        this.gpuDecoderUsage = gpuDecoderUsage;
    }

}
