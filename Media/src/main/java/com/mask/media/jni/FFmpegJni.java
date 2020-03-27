package com.mask.media.jni;

import java.util.List;

public class FFmpegJni {
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg-cmd");
    }

    /**
     * 执行FFmpeg命令
     *
     * @param cmdLen cmdLen
     * @param cmd    cmd
     * @return int
     */
    private static native int run(int cmdLen, String[] cmd);

    /**
     * 获取命令执行进度
     *
     * @return int
     */
    public static native int getProgress();

    /**
     * 获取转码速率
     *
     * @return double
     */
    public static native double getSpeed();

    /**
     * 获取文件信息
     *
     * @param path path
     * @return String
     */
    public static native String getMediaInfo(String path);

    /**
     * 执行FFmpeg命令，同步模式
     *
     * @param cmd 命令
     * @return int
     */
    public static int run(List<String> cmd) {
        String[] cmdArr = new String[cmd.size()];
        return run(cmd.toArray(cmdArr));
    }

    /**
     * 执行FFmpeg命令，同步模式
     *
     * @param cmd 命令
     * @return int
     */
    public static int run(String[] cmd) {
        return run(cmd.length, cmd);
    }

}
