package com.mask.media.model;

public class MediaInfo {
    public int width;
    public int height;
    public long duration;
    public int bitrate;
    public int fps;
    public int rotation;
    public String videoCodec;

    @Override
    public String toString() {
        return "MediaInfo{" +
                "width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", bitrate=" + bitrate +
                ", fps=" + fps +
                ", rotation=" + rotation +
                ", videoCodec='" + videoCodec + '\'' +
                '}';
    }
}