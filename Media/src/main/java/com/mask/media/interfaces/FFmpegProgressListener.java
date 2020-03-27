package com.mask.media.interfaces;

/**
 * FFmpeg 进度监听
 * Created by lishilin on 2020/03/27
 */
public abstract class FFmpegProgressListener {

    /**
     * 进度更新
     *
     * @param progress      progress
     * @param timeRemaining 剩余时间
     */
    public void onProgressUpdate(int progress, long timeRemaining) {

    }

}
