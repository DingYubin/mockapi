package com.yubin.mvx.databinding.base;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;


/**
 * <pre>
 *     @author : dingyubin
 *     time   : 2017/08/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class BaseActivity<SV extends ViewDataBinding> extends DaggerAppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    /**
     * 布局
     */
    protected SV bindingView;
    private final Set<BaseViewModel> vmSet = new HashSet<>();

    /**
     * 获取布局文件id
     *
     * @return 布局文件id
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化viewModel
     */
    protected abstract void initViewModel();

    /**
     * 初始化
     */
    protected abstract void init();


    protected <T extends BaseViewModel> T getViewModel(Class<T> tClass) {

        T baseViewModel = new ViewModelProvider(this, viewModelFactory).get(tClass);
        addViewModel(baseViewModel);
        return baseViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, getLayoutId());
        initViewModel();
        init();

    }

    protected void addViewModel(BaseViewModel baseViewModel) {
        addViewModel(baseViewModel, true);
    }

    protected void addViewModel(BaseViewModel baseViewModel, boolean initNow) {
        if (vmSet.add(baseViewModel)) {
            if (initNow) {
                baseViewModel.init();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (BaseViewModel viewModel : vmSet) {
            viewModel.removeSubscription();
        }
    }
}
