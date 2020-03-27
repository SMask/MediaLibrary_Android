package com.mask.media.utils;

import com.mask.media.interfaces.FFmpegProgressListener;
import com.mask.media.jni.FFmpegJni;
import com.mask.media.model.MediaInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * FFmpeg 工具类
 * Created by lishilin on 2020/03/27
 */
public class FFmpegUtils {

    /**
     * 获取文件信息
     *
     * @param path path
     * @return VideoInfo
     */
    public static MediaInfo getMediaInfo(String path) {
        JSONObject dataObj = null;
        try {
            dataObj = new JSONObject(FFmpegJni.getMediaInfo(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaInfo info = new MediaInfo();
        if (dataObj != null) {
            info.width = dataObj.optInt("width");
            info.height = dataObj.optInt("height");
            info.duration = dataObj.optLong("duration");
            info.bitrate = dataObj.optInt("bitrate");
            info.fps = dataObj.optInt("fps");
            info.rotation = dataObj.optInt("rotation");
            info.videoCodec = dataObj.optString("videoCodec");
        }
        return info;
    }

    /**
     * 注册监听器
     *
     * @param info     视频信息
     * @param listener 监听
     */
    private static void registerListener(final MediaInfo info, final FFmpegProgressListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int frame = -1;
                boolean started = false;
                while (frame != 0) {
                    int frameTemp = FFmpegJni.getProgress();
                    int progress;
                    if (frameTemp > 0) {
                        started = true;
                    }
                    if (started) {
                        frame = frameTemp;
                        progress = (int) Math.ceil(frame * 100.0f / (info.fps * info.duration / 1000.0f));
                        double speed = FFmpegJni.getSpeed();
                        long timeRemaining = 0;
                        if (speed > 0) {
                            timeRemaining = (long) (info.duration * (1 - progress / 100.0) / speed);
                        }
                        listener.onProgressUpdate(progress, timeRemaining);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 裁切视频
     *
     * @param srcPath  视频源路径
     * @param outPath  视频输出路径
     * @param startX   startX
     * @param startY   startY
     * @param width    裁切宽
     * @param height   裁切高
     * @param info     视频信息
     * @param listener 监听
     */
    public static void crop(String srcPath, String outPath, final int startX, int startY, int width, int height, final MediaInfo info, final FFmpegProgressListener listener) {
        registerListener(info, listener);
        crop(srcPath, outPath, startX, startY, width, height, info);
    }

    /**
     * 裁切视频
     *
     * @param srcPath 视频源路径
     * @param outPath 视频输出路径
     * @param startX  startX
     * @param startY  startY
     * @param width   裁切宽
     * @param height  裁切高
     * @param info    视频信息
     */
    private static void crop(String srcPath, String outPath, final int startX, int startY, int width, int height, final MediaInfo info) {
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(srcPath);
        cmd.add("-vf");
        cmd.add("crop=" + width + ":" + height + ":" + startX + ":" + startY);
        cmd.add(outPath);
        FFmpegJni.run(cmd);
    }

}
