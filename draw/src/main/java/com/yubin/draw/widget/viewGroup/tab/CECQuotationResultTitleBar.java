package com.yubin.draw.widget.viewGroup.tab;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yubin.baselibrary.util.LogUtil;
import com.yubin.draw.R;

/**
 * 报价结果页titleBar
 *
 */
public class CECQuotationResultTitleBar extends FrameLayout{
    private Context mContext;
    public CECTabLayout tabLayout;

    private ViewPager2 viewPager2;
//    private OnOperateBtnClickListener mOperateBtnClickListener;

    public CECQuotationResultTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public CECQuotationResultTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CECQuotationResultTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflaterViewWithContext(context);
    }

    private void inflaterViewWithContext(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.inquiry_widget_quotation_result_title_bar, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).onBackPressed();
            }
        });
//        tabLayout = findViewById(R.id.tab_button);
        LogUtil.i("onFinishInflate tabLayout1 " + tabLayout);
    }

    public void showTabButton() {
        tabLayout.setVisibility(VISIBLE);
    }

    public void hideTabButton() {
        tabLayout.setVisibility(GONE);
    }

    public void setSelectTab(int index) {
        int tabCount = tabLayout.getTabCount();
        if (index > tabCount) return;
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tabLayout.selectTab(tab);
    }
    final String[] tabs = new String[]{"按配件", "按供应商"};
    public void setViewPager(ViewPager2 viewPager) {
        tabLayout = findViewById(R.id.tab_button);
        LogUtil.i("onFinishInflate tabLayout2 " + tabLayout);
        this.viewPager2 = viewPager;
        this.viewPager2.registerOnPageChangeCallback(changeCallback);
        LogUtil.i("onFinishInflate");
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            LogUtil.i("onFinishInflate tabLayout3 " + tabLayout);
            LogUtil.i(" CECQuotationResultTitleBar TabLayoutMediator tabs text = " + tabs[position]);
            CECTabView tabView = new CECTabView(getContext());
            ColorStateList colorStateList = AppCompatResources.getColorStateList(getContext(), R.color.inquiry_selector_cec_color_tab_text_new);
            tab.setCustomView(tabView);
            tabView.setText(tabs[position]);
            tabView.setTabTextColor(colorStateList);
//            tab.setText("Tab " + position);
//            tabLayout.addTab(tab);
        }).attach();
    }


    private ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            LogUtil.i("CECQuotationResultTitleBar onPageSelected position = " + position);
//            setSelectTab(position);
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab == null) {
                    return;
                }

                CECTabView customerView = (CECTabView) tab.getCustomView();

                if (customerView == null) {
                    return;
                }

                LogUtil.i("CECQuotationResultTitleBar tab.getPosition() == position = " + (tab.getPosition() == position));
                customerView.isSelected(tab.getPosition() == position);
            }
        }
    };



//    public interface OnOperateBtnClickListener {
//
//        /**
//         * 切换Tab
//         */
//        void onTabSelected(int index);
//    }
}
