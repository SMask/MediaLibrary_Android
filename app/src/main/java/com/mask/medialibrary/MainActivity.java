package com.mask.medialibrary;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mask.media.interfaces.FFmpegProgressListener;
import com.mask.media.model.MediaInfo;
import com.mask.media.utils.FFmpegUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText edt_startX;
    private EditText edt_startY;
    private EditText edt_width;
    private EditText edt_height;
    private View btn_crop_start;

    private TextView tv_source_info;
    private TextView tv_progress;
    private TextView tv_target_info;

    private File sourceFile;
    private File targetFile;

    private MediaInfo sourceInfo;
    private MediaInfo targetInfo;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        edt_startX = findViewById(R.id.edt_startX);
        edt_startY = findViewById(R.id.edt_startY);
        edt_width = findViewById(R.id.edt_width);
        edt_height = findViewById(R.id.edt_height);
        btn_crop_start = findViewById(R.id.btn_crop_start);
        tv_source_info = findViewById(R.id.tv_source_info);
        tv_progress = findViewById(R.id.tv_progress);
        tv_target_info = findViewById(R.id.tv_target_info);
    }

    private void initData() {
        // 创建保存路径
        final File dirFile = FileUtils.getCacheMovieDir(this);
        boolean mkdirs = dirFile.mkdirs();
        // 创建保存文件
        sourceFile = new File(dirFile, "MediaRecorder_20200327_144432966.mp4");
        targetFile = new File(dirFile, FileUtils.getDateName("FFmpeg") + ".mp4");

        sourceInfo = FFmpegUtils.getMediaInfo(sourceFile.getAbsolutePath());
        tv_source_info.setText("Source: " + getMediaInfo(sourceInfo, sourceFile));
    }

    private void setListener() {
        btn_crop_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_crop_start.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        crop();
                    }
                }).start();
            }
        });
    }

    /**
     * 获取 媒体信息
     *
     * @param info info
     * @param file file
     * @return String
     */
    private String getMediaInfo(MediaInfo info, File file) {
        return "{" +
                "width=" + info.width +
                ", height=" + info.height +
                ", duration=" + info.duration +
                ", bitrate=" + info.bitrate +
                ", fps=" + info.fps +
                ", rotation=" + info.rotation +
                ", videoCodec='" + info.videoCodec +
                ", path='" + file.getAbsolutePath() +
                "}";
    }

    /**
     * 获取 输入Value
     *
     * @param edt_input edt_input
     * @return int
     */
    private int getEditTextValue(EditText edt_input) {
        try {
            return Integer.parseInt(edt_input.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 裁切
     */
    private void crop() {
        int startX = getEditTextValue(edt_startX);
        int startY = getEditTextValue(edt_startY);
        int width = getEditTextValue(edt_width);
        int height = getEditTextValue(edt_height);
        FFmpegUtils.crop(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), startX, startY, width, height, sourceInfo, new FFmpegProgressListener() {
            @Override
            public void onProgressUpdate(final int progress, final long timeRemaining) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(tv_progress.getText())) {
                            tv_progress.append("\n");
                        }
                        tv_progress.append("progress: " + progress + " timeRemaining: " + timeRemaining);
                    }
                });
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_crop_start.setEnabled(true);
                targetInfo = FFmpegUtils.getMediaInfo(targetFile.getAbsolutePath());
                tv_target_info.setText("Target: " + getMediaInfo(targetInfo, targetFile));
            }
        }, 1000);
    }
}
