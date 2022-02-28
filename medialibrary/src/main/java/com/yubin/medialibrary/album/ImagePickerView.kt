package com.yubin.medialibrary.album

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yubin.medialibrary.album.adapter.ImagePickerAdapter
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.decoration.ImagePickerItemDecoration

/**
 *description
 */
class ImagePickerView : RecyclerView {

    private var adapter: ImagePickerAdapter? = null

    constructor(context: Context?) : super(context!!) {
        inflateContentViewWithContext(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        inflateContentViewWithContext(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        inflateContentViewWithContext(context)
    }

    private fun inflateContentViewWithContext(context: Context) {
        setBackgroundColor(Color.rgb(0xf1, 0xf2, 0xf3))
        addItemDecoration(
            ImagePickerItemDecoration(
                context
            )
        )
        layoutManager = GridLayoutManager(context, 4)
    }

    /**
     * 设置数据源
     */
    fun setData(list: List<ImagePickerBean>?, listener: ImageSelectListener?) {
        if (null == adapter) {
            adapter = ImagePickerAdapter(listener)
            adapter
            setAdapter(adapter)
        }
        adapter?.submitList(list)
    }

}