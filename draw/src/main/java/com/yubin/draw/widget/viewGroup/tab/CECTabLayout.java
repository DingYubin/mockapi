package com.yubin.draw.widget.viewGroup.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

public class CECTabLayout extends TabLayout {

    public CECTabLayout(Context context) {
        this(context, null);
    }

    public CECTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CECTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.initWithContext(context);
    }

    /**
     * 初始化
     *
     * @param context {@link Context}
     */
    private void initWithContext(Context context) {
        this.addOnTabSelectedListener(new InnerTabSelectedListener());
    }

    /**
     * 根据文字添加tab
     *
     * @param text tab文字
     */
    public void addTab(String text) {
        CECTabView tabView = new CECTabView(getContext());
        Tab tab = this.newTab();
        tab.setCustomView(tabView);
        tabView.setText(text);
        this.addTab(tab);
    }

    /**
     * 根据文字添加tab 指定选择颜色
     *
     * @param text tab文字
     */
    public void addTab(String text, ColorStateList colors) {
        CECTabView tabView = new CECTabView(getContext());
        Tab tab = this.newTab();
        tab.setCustomView(tabView);
        tabView.setText(text);
        tabView.setTabTextColor(colors);
        this.addTab(tab);
    }

    public void addTab(String text, ColorStateList colors, Tab tab) {
        CECTabView tabView = new CECTabView(getContext());
        tabView.setText(text);
        tabView.setTabTextColor(colors);
        tab.setCustomView(tabView);
//        this.addTab(tab);
    }


    /**
     * 根据文字添加tab 指定选择颜色
     *
     * @param text tab文字
     */
    public void addTab(String text, ColorStateList colors, boolean indicatorVisible) {
        CECTabView tabView = new CECTabView(getContext());
        Tab tab = this.newTab();
        tab.setCustomView(tabView);
        tabView.setText(text);
        tabView.setTabTextColor(colors);
        tabView.setIndicatorVisible(indicatorVisible);
        this.addTab(tab);
    }

    /**
     * 添加tabs
     *
     * @param texts tabs的文字
     */
    public void addTabs(String[] texts) {
        if (texts == null || texts.length == 0) {
            return;
        }
        List<String> targetArray = Arrays.asList(texts);
        this.addTabs(targetArray);
    }

    /**
     * 添加tabs
     *
     * @param texts tabs的文字
     */
    public void addTabs(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return;
        }
        for (String text : texts) {
            this.addTab(text);
        }
    }

    /**
     * 设置数量徽章
     *
     * @param position 位置索引
     * @param num      数字
     */
    public void setNumBadge(int position, int num) {
        int tabCount = this.getTabCount();
        if (tabCount <= 0 || position >= tabCount) {
            return;
        }
        Tab tab = this.getTabAt(position);
        if (tab == null) {
            return;
        }
        View customerView = tab.getCustomView();
        if (customerView == null) {
            return;
        }
        if (!(customerView instanceof CECTabView)) {
            return;
        }
        ((CECTabView) customerView).setNumBadge(num);
    }

    /**
     * tab选中监听事件
     */
    private class InnerTabSelectedListener implements OnTabSelectedListener {

        @Override
        public void onTabSelected(Tab tab) {
            if (tab == null) {
                return;
            }
            View customerView = tab.getCustomView();
            if (customerView == null) {
                return;
            }
            if (!(customerView instanceof CECTabView)) {
                return;
            }
            ((CECTabView) customerView).isSelected(true);
        }

        @Override
        public void onTabUnselected(Tab tab) {
            if (tab == null) {
                return;
            }
            View customerView = tab.getCustomView();
            if (customerView == null) {
                return;
            }
            if (!(customerView instanceof CECTabView)) {
                return;
            }
            ((CECTabView) customerView).isSelected(false);
        }

        @Override
        public void onTabReselected(Tab tab) {
        }
    }
}
