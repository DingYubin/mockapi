package com.yubin.imagepicker.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.yubin.imagepicker.ImagePicker;
import com.yubin.imagepicker.R;
import com.yubin.imagepicker.util.Utils;
import com.yubin.imagepicker.view.VideoPlayer;

import java.io.File;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ImageVideoPlayActivity extends AppCompatActivity {
    private String videoPath = "";
    private String videoName = "";
    VideoPlayer videoPlayer;
    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video_play);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        videoPath = getIntent().getStringExtra("video_path");
        videoName = getIntent().getStringExtra("video_name");
        if (TextUtils.isEmpty(videoPath)) {
            finish();
            return;
        }
        if (!videoPath.startsWith("http")) {
            File file = new File(videoPath);
            if (file == null || !file.exists() || file.length() == 0) {
                finish();
                return;
            }
        }
        init();
    }

    private void init() {
        videoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        videoPlayer.setUp(videoPath, true, TextUtils.isEmpty(videoName) ? "视频预览" : videoName);
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        DisplayMetrics dm = Utils.getScreenPix(this);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (ImagePicker.getInstance().getImageLoader() != null) {
            ImagePicker.getInstance().getImageLoader().displayVideoPreview(this, videoPath, imageView, screenWidth, screenHeight);
        }
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
