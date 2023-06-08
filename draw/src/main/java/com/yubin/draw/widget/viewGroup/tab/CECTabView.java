package com.yubin.draw.widget.viewGroup.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yubin.draw.R;


public class CECTabView extends LinearLayout {

    private TextView mDot;
    private View mIndicator;
    private TextView mTabText;

    public CECTabView(Context context) {
        super(context);
        this.inflateContentViewWithContext(context);
    }

    public CECTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inflateContentViewWithContext(context);
    }

    public CECTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.inflateContentViewWithContext(context);
    }

    /**
     * 设置数量徽章
     *
     * @param num 数量
     */
    @SuppressLint("SetTextI18n")
    public void setNumBadge(int num) {
        if (mDot == null) {
            return;
        }
        if (num <= 0) {
            mDot.setVisibility(View.INVISIBLE);
            return;
        }
        if (!mDot.isShown()) {
            mDot.setVisibility(View.VISIBLE);
        }
        if (num >= 100) {
            mDot.setText("99+");
            return;
        }
        mDot.setText(String.valueOf(num));
    }

    /**
     * 添加文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        if (mTabText == null) {
            return;
        }
        mTabText.setText(text);
    }

    public void setTabTextColor(int color) {
        if (mTabText != null) {
            mTabText.setTextColor(color);
        }
    }

    public void setTabTextColor(ColorStateList colors) {
        if (mTabText != null) {
            mTabText.setTextColor(colors);
        }
    }

    /**
     * 设置是否选中
     *
     * @param isSelect 是否选中
     */
    public void isSelected(boolean isSelect) {
        if (mTabText != null) {
            mTabText.setSelected(isSelect);
            mTabText.setTextSize(TypedValue.COMPLEX_UNIT_SP, isSelect ? 16 : 15);
            mTabText.setTypeface(isSelect ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        }
        if (mIndicator != null) {
            mIndicator.setSelected(isSelect);
        }
    }

    public void setIndicatorVisible(boolean visible) {
        if (mIndicator != null) {
            if (visible) {
                mIndicator.setVisibility(View.VISIBLE);
            } else {
                mIndicator.setVisibility(View.GONE);
            }
        }
        if (mDot != null) {
            if (visible) {
                mDot.setVisibility(VISIBLE);
            } else {
                mDot.setVisibility(GONE);
            }
        }
    }

    /**
     * 渲染内容视图
     *
     * @param context {@link Context}
     */
    private void inflateContentViewWithContext(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null within CECTabView");
        }
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.uikit_widget_cec_tab_view_layout, this, true);
        mIndicator = this.findViewById(R.id.widget_cec_tab_view_indicator);
        mTabText = this.findViewById(R.id.widget_cec_tab_view_text);
        mDot = this.findViewById(R.id.widget_cec_tab_view_dot);
    }
}
