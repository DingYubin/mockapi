package com.yubin.baselibrary.ui.toolBar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.yubin.baselibrary.databinding.PlatformWidgetToolBarBinding

/**
 * description: 开思默认的toolBar的生成器
 */
class CassToolBarBuilder(context: Context?) : ICassToolBarBuilder {
    private var binding: PlatformWidgetToolBarBinding =
        PlatformWidgetToolBarBinding.inflate(LayoutInflater.from(context), null, false)

    override fun buildToolBar(): Toolbar {
        return binding.widgetToolBar
    }

    override fun setTitle(title: String?) {
        binding.toolBarTitle.text = title
    }

    override fun setTitle(resId: Int) {
        binding.toolBarTitle.setText(resId)
    }

    override fun setTitleColor(color: Int) {
        //The subclass implementation
    }

    override fun setNavigationIcon(resId: Int) {
        //The subclass implementation
    }

    override fun setTitleOnClickListener(onClickListener: View.OnClickListener?) {
        binding.toolBarTitle.setOnClickListener(onClickListener)
    }

}