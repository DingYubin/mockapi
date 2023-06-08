package com.yubin.draw.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yubin.baselibrary.router.path.RouterPath;
import com.yubin.baselibrary.ui.basemvvm.NativeActivity;
import com.yubin.baselibrary.util.LogUtil;
import com.yubin.draw.R;
import com.yubin.draw.adapter.CECQuotationFragmentStateAdapter;
import com.yubin.draw.databinding.ActivityTabBinding;
import com.yubin.draw.widget.viewGroup.tab.CECTabView;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/28
 *     desc    : rx测试页面
 *     version : 1.0
 * </pre>
 */

@Route(path = RouterPath.UiPage.PATH_UI_TAB)
public class TabActivity extends NativeActivity<ActivityTabBinding> {

    @NonNull
    @Override
    public ActivityTabBinding getViewBinding() {
        return ActivityTabBinding.inflate(getLayoutInflater());
    }
    final String[] tabs = new String[]{"按配件", "按供应商"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TestFragment1());
        fragments.add(new TestFragment2());

        getBinding().viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        getBinding().viewPager.setAdapter(new CECQuotationFragmentStateAdapter(this, fragments));

        getBinding().titleBar.setViewPager(getBinding().viewPager);

//     bindingTab()



    }

    private void bindingTab() {
        getBinding().viewPager.registerOnPageChangeCallback(changeCallback);
        new TabLayoutMediator(getBinding().tabButton, getBinding().viewPager, (tab, position) -> {
            LogUtil.i(" TabActivity TabLayoutMediator tabs text = " + tabs[position]);
            CECTabView tabView = new CECTabView(this);
            ColorStateList colorStateList = AppCompatResources.getColorStateList(this, R.color.inquiry_selector_cec_color_tab_text_new);
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
            LogUtil.i("TabActivity onPageSelected position = " + position);
//            setSelectTab(position);
            int tabCount = getBinding().tabButton.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = getBinding().tabButton.getTabAt(i);
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
}
