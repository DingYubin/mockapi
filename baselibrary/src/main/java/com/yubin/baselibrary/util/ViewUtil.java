package com.yubin.baselibrary.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**
 * Created by zhaojieb9 on 2017/7/31.
 */

public class ViewUtil {
    private static final int DP = 0;
    private static long lastClickTime;

    public static void visiable(View view) {
        if (view == null) return;
        view.setVisibility(View.VISIBLE);
    }

    public static void invisiable(View view) {
        if (view == null) return;
        view.setVisibility(View.INVISIBLE);
    }

    public static void gone(View view) {
        if (view == null) return;
        view.setVisibility(View.GONE);
    }

    public static boolean isVisible(View view) {
        if (view == null) return false;
        return view.getVisibility() == View.VISIBLE;
    }

    public static boolean isInvisible(View view) {
        if (view == null) return false;
        return view.getVisibility() == View.INVISIBLE;
    }

    public static boolean isGone(View view) {
        if (view == null) return false;
        return view.getVisibility() == View.GONE;
    }

    @NonNull
    public static TypedValue getTypedValue(@NonNull Context context, @AttrRes int resId) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(resId, tv, true);
        return tv;
    }

    @ColorInt
    public static int getColor(@NonNull Context context, @AttrRes int color) {
        return getTypedValue(context, color).data;
    }

    @DrawableRes
    public static int getDrawableRes(@NonNull Context context, @AttrRes int drawable) {
        return getTypedValue(context, drawable).resourceId;
    }

    /**
     * Converts dps to pixels nicely.
     *
     * @param context the Context for getting the resources
     * @param dp dimension in dps
     * @return dimension in pixels
     */
    public static int dpToPixel(@NonNull Context context, @Dimension(unit = DP) float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        try {
            return (int) (dp * metrics.density);
        } catch (NoSuchFieldError ignored) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }
    }

    /**
     * Converts pixels to dps just as well.
     *
     * @param context the Context for getting the resources
     * @param px dimension in pixels
     * @return dimension in dps
     */
    public static int pixelToDp(@NonNull Context context, @Px int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / displayMetrics.density);
    }

    /**
     * Returns screen width.
     *
     * @param context Context to get resources and device specific display metrics
     * @return screen width
     */
    public static int getScreenWidth(@NonNull Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    /**
     * A convenience method for setting text appearance.
     *
     * @param textView a TextView which textAppearance to modify.
     * @param resId a style resource for the text appearance.
     */
    @SuppressWarnings("deprecation")
    public static void setTextAppearance(
            @NonNull TextView textView, @StyleRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    /**
     * Determine if the current UI Mode is Night Mode.
     *
     * @param context Context to get the configuration.
     * @return true if the night mode is enabled, otherwise false.
     */
    public static boolean isNightMode(@NonNull Context context) {
        int currentNightMode =
                context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean chatViewSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = layoutManager.findLastVisibleItemPosition();
        return first == 0 || first == 1;
    }

    /**
     * 防止快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (400 < timeD) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 防止快速点击
     */
    public static boolean isFastDoubleClick(long spaceTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        Log.d("llw",
                "isFastDoubleClick(long spaceTime) spaceTime=" + spaceTime + "  timeD=" + timeD);

        if (spaceTime < timeD) {
            lastClickTime = time;
            return false;
        }
        //        lastClickTime = time;
        return true;
    }
}
