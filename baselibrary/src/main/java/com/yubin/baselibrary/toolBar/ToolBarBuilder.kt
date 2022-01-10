package com.yubin.baselibrary.toolBar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.yubin.baselibrary.databinding.UikitWidgetToolBarBinding

/**
 * description: 默认的toolBar的生成器
 */
class ToolBarBuilder(context: Context?) : IToolBarBuilder {
    private var binding: UikitWidgetToolBarBinding =
        UikitWidgetToolBarBinding.inflate(LayoutInflater.from(context), null, false)

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