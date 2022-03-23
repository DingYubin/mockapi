package com.yubin.baselibrary.impl;

import android.view.View;

/**
 * Created by WenChang Mai on 2019/3/13 17:42.
 * Description: 防抖动点击事件监听器
 * {@see https://github.com/JakeWharton/butterknife }
 * copy from butterknife/butterknife-runtime/src/main/java/butterknife/internal/DebouncingOnClickListener.java
 * which written by JakeWharton
 */
public abstract class DebounceOnClickListener implements View.OnClickListener {

    private static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = () -> enabled = true;

    @Override
    public final void onClick(View v) {
        if (!enabled) {
            return;
        }
        enabled = false;
        v.postDelayed(ENABLE_AGAIN, 200);
        doClick(v);
    }

    public abstract void doClick(View v);
}
