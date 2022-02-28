package com.yubin.medialibrary.album.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.IImageFolderSelectListener
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.extra.BaseViewHolder

/**
 *description 照片文件夹列表适配器ViewHolder
 *
 *@author laiwei
 *@date create at 4/24/21 11:23 AM
 */
class ImagePickerFolderViewHolder(private val mItemView: View) :
    BaseViewHolder<ImagePickerBean?>(mItemView) {
    private val mImageUrlView: AppCompatImageView =
        mItemView.findViewById(R.id.adapter_item_folder_image)
    private val mImageFolderName: AppCompatTextView =
        mItemView.findViewById(R.id.adapter_item_folder_name)
    private val mImageCount: AppCompatTextView =
        mItemView.findViewById(R.id.adapter_item_folder_count)
    private val mFolderCheck: AppCompatImageView =
        mItemView.findViewById(R.id.adapter_item_folder_check)

    private var mImageFolderSelectListener: IImageFolderSelectListener? = null
    override fun bindDataFully(data: ImagePickerBean, position: Int, count: Int) {
        var bucketDisplayName = data.bucketDisplayName
        if (null == bucketDisplayName) {
            bucketDisplayName = ""
        }
        mImageFolderName.text = bucketDisplayName
        if (data.isVideo) {
            ImageLoaderManager.INSTANCE.intoTarget(data.thumbData, data.data, mImageUrlView)
        } else {
            ImageLoaderManager.INSTANCE.intoTarget(data.data, mImageUrlView)
        }
        MediaManager.instance.mediaStyle?.let {
            mFolderCheck.setImageResource(it.folderSelectImage)
        }
        mFolderCheck.visibility = if (data.isFolderSelect) View.VISIBLE else View.GONE
        mImageCount.text = "（${data.currentFolderImageNumber}）"
        mItemView.setOnClickListener {
            mFolderCheck.visibility = View.VISIBLE
            if (data.isVideo) {
                mImageFolderSelectListener?.imageFolderSelect(data.parentFolder, position)
            } else {
                mImageFolderSelectListener?.imageFolderSelect(data.parentFolder, position)
            }
        }
    }

    override fun bindDiffData(
        data: ImagePickerBean,
        payload: Bundle,
        position: Int,
        count: Int
    ) {
        //bindDiffData
    }

    fun setImageFolderSelectListener(listener: IImageFolderSelectListener?) {
        mImageFolderSelectListener = listener
    }

}