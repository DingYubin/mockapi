package com.yubin.medialibrary.photoPreview

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.extra.BaseViewHolder

/**
 * description
 *
 * @author laiwei
 * @date create at 2019-07-08 18:05
 */
class ImagePreviewHolder(itemView: View, private val mPickerAdapter: ImagePreviewAdapter) :
    BaseViewHolder<ImagePickerBean?>(itemView) {
    private var mImageUrlView: AppCompatImageView = itemView.findViewById(R.id.image_picker_view)
    private var mImageUrlViewBg: AppCompatImageView =
        itemView.findViewById(R.id.image_picker_view_bg)
    private var mImageVideoTag: AppCompatImageView =
        itemView.findViewById(R.id.image_picker_video_tag)

    override fun bindDataFully(data: ImagePickerBean, position: Int, count: Int) {
        if (data.isVideo) {
            mImageVideoTag.visibility = View.VISIBLE
            ImageLoaderManager.INSTANCE.intoTarget(data.thumbData, data.data, mImageUrlView)
        } else {
            mImageVideoTag.visibility = View.GONE
            ImageLoaderManager.INSTANCE.intoTarget(data.data, mImageUrlView)
        }
        if (data.isSelect) {
            mImageUrlViewBg.setBackgroundResource(
                MediaManager.instance.mediaStyle?.previewSelectImage
                    ?: 0
            )
        } else {
            mImageUrlViewBg.setBackgroundColor(Color.TRANSPARENT)
        }
        itemView.onViewClick {
            mPickerAdapter.listener?.imagePreviewSelect(data, position)
        }
    }

    override fun bindDiffData(
        data: ImagePickerBean,
        payload: Bundle,
        position: Int,
        count: Int
    ) {

    }


}