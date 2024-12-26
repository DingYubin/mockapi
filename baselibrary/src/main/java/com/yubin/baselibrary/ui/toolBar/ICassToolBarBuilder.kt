package com.yubin.baselibrary.ui.toolBar

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

/**
 * description: 开思toolBar的生成接口定义
 */
interface ICassToolBarBuilder {
    /**
     * toolBar的生成
     */
    fun buildToolBar(): Toolbar?

    /**
     * 设置title
     */
    fun setTitle(title: String?)

    /**
     * 设置title
     */
    fun setTitle(@StringRes resId: Int)

    /**
     * 设置title
     */
    fun setTitleColor(@ColorInt color: Int)

    /**
     * 设置title
     */
    fun setNavigationIcon(@DrawableRes resId: Int)

    /**
     * 设置title点击事件
     *
     * @param onClickListener
     */
    fun setTitleOnClickListener(onClickListener: View.OnClickListener?)
}