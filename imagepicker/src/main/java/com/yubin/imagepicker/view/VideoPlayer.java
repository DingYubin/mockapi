package com.yubin.imagepicker.view;

import android.content.Context;
import android.util.AttributeSet;

import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.yubin.imagepicker.R;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class VideoPlayer extends NormalGSYVideoPlayer {
    public VideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_video_player;
    }

}
