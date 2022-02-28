package com.yubin.medialibrary.album.adapter

import android.view.ViewGroup
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.ImageSelectListener
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.extra.BaseAdapter

/**
 *description
 *
 *@author laiwei
 */
class ImagePickerAdapter(listener: ImageSelectListener?) :
    BaseAdapter<ImagePickerBean, ImagePickerViewHolder>() {

    private var mSelectListener: ImageSelectListener? = listener

    private var selectIndex = 0

    fun getSelectIndex(): Int {
        return selectIndex
    }

    fun setSelectIndex(selectIndex: Int) {
        this.selectIndex = selectIndex
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ImagePickerViewHolder {
        val itemView = getLayoutInflater(viewGroup).inflate(
            R.layout.adapter_image_picker_item,
            viewGroup,
            false
        )
        val viewHolder = ImagePickerViewHolder(itemView, this)
        viewHolder.setImageSelectListener(mSelectListener)
        return viewHolder
    }


}