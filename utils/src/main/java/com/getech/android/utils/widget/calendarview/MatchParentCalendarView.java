package com.getech.android.utils.widget.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

/**
 * <pre>
 *     @author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2019-2-26
 *     desc   : 修复宽度bug的MaterialCalendarView
 *     version: 1.0
 * </pre>
 */

public class MatchParentCalendarView extends MaterialCalendarView {
    public MatchParentCalendarView(Context context) {
        super(context);
    }

    public MatchParentCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentWidth = right - left - parentLeft - getPaddingRight();

        int childTop = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            if (child instanceof LinearLayout) {
                width = parentWidth;
            }
            final int finalWidth = width;
            final int finalHeight = height;

            int delta = (parentWidth - finalWidth) / 2;
            int childLeft = parentLeft + delta;

            child.layout(childLeft, childTop, childLeft + finalWidth, childTop + finalHeight);

            childTop += height;
        }
    }
}
