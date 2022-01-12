package com.yubin.baselibrary.ui.basemvvm

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.viewbinding.ViewBinding

/**
 * Description
 * @Date 2020/7/30 5:30 PM
 */
abstract class LazLoadFragment<VB : ViewBinding> : BaseFragment<VB>() {

    /**
     * 是否已经准备好
     */
    private var mIsPrepared = false

    /**
     * 懒加载过
     */
    private var mIsLazyLoaded = false

    /**
     * lazy加载数据
     */
    abstract fun onLazyLoadData()

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsPrepared = true
        launchLazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        launchLazyLoad()
    }

    /**
     * 发起lazy加载
     */
    private fun launchLazyLoad() {
        if (!mIsPrepared) {
            return
        }
        if (mIsLazyLoaded) {
            return
        }
        if (!this.userVisibleHint) {
            return
        }
        mIsLazyLoaded = true
        onLazyLoadData()
    }
}