package com.yubin.draw.widget.viewGroup.im

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * description 增加监听输入框里的复制、剪切、粘贴事件
 */
class CustomAppCompatEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatEditText(context, attrs, defStyle) {
    private var listener: ((id: Int) -> Unit)? = null

    override fun onTextContextMenuItem(id: Int): Boolean {
        listener?.invoke(id)
        return super.onTextContextMenuItem(id)
    }

    fun addTextContextMenuListener(listener: (id: Int) -> Unit) {
        this.listener = listener
    }
}