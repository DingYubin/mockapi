package com.yubin.baselibrary.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;


/**
 * description: 开思软键盘辅助类
 */
public class KeyBoardHelper {

    /**
     * 键盘是否弹出
     */
    public static boolean isActive(@Nullable Context context) {
        if (context == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static boolean isActive(@Nullable Context context, View view) {
        if (context == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(view);
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(@Nullable Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = null;
        Activity activity = CECAppHelper.scanForActivity(context);
        if (activity != null) {
            view = activity.getWindow().peekDecorView();
        }
        if (null != view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 是否需要隐藏键盘
     */
    public static boolean isNeedHideKeyboard(View view, MotionEvent event) {
        if (view == null || event == null) {
            return false;
        }
        if (!(view instanceof EditText)) {
            return false;
        }
        int[] l = {0, 0};
        view.getLocationInWindow(l);
        int left = l[0];
        int top = l[1];
        int bottom = top + view.getHeight();
        int right = left + view.getWidth();
        return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
    }

    /**
     * 打开软键盘
     */
    public static void openKeyBoard(@Nullable Context context, @Nullable EditText editText) {
        if (context == null || editText == null) {
            return;
        }
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 设置软键盘打开或者关闭的事件监听
     */
    public static void addKeyBoardShowListener(OnKeyboardVisibilityListener listener, Activity activity) {
        final View activityRootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean wasOpened;
            private static final int DefaultKeyboardDP = 100;
            // From @nathanielwolf answer... Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
            private final int EstimatedKeyboardDP = DefaultKeyboardDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                // Convert the dp to pixels.
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, activityRootView.getResources().getDisplayMetrics());
                // Conclude whether the keyboard is shown or not.
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;
                if (isShown == wasOpened) {
                    return;
                }
                wasOpened = isShown;
                listener.onVisibilityChanged(isShown);
            }
        });
    }

    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

    private KeyBoardHelper() {
    }
}
