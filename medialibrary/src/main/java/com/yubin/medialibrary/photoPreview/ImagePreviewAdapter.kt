package com.yubin.medialibrary.photoPreview

import android.view.ViewGroup
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.extra.BaseAdapter

/**
 *description
 *
 *@author laiwei
 *@date create at 4/24/21 11:28 AM
 */
class ImagePreviewAdapter(var listener: ImagePreviewListener?) :
    BaseAdapter<ImagePickerBean, ImagePreviewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ImagePreviewHolder {
        val itemView = getLayoutInflater(viewGroup).inflate(
            R.layout.preview_item_view,
            viewGroup, false
        )
        return ImagePreviewHolder(itemView, this)
    }


}