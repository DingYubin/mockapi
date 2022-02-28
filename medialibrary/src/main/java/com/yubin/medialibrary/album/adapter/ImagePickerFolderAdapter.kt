package com.yubin.medialibrary.album.adapter

import android.view.ViewGroup
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.IImageFolderSelectListener
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.extra.BaseAdapter

/**
 *description 照片文件夹列表适配器
 *
 *@author laiwei
 *@date create at 4/24/21 11:22 AM
 */
class ImagePickerFolderAdapter(private val mImageFolderSelectListener: IImageFolderSelectListener) :
    BaseAdapter<ImagePickerBean?, ImagePickerFolderViewHolder?>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): ImagePickerFolderViewHolder {
        val itemView =
            getLayoutInflater(viewGroup).inflate(
                R.layout.adapter_image_picker_folder_item,
                viewGroup, false
            )
        val viewHolder = ImagePickerFolderViewHolder(itemView)
        viewHolder.setImageFolderSelectListener(mImageFolderSelectListener)
        return viewHolder
    }

}