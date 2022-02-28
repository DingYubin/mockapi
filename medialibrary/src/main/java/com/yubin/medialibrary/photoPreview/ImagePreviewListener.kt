package com.yubin.medialibrary.photoPreview

import com.yubin.medialibrary.album.been.ImagePickerBean


interface ImagePreviewListener {
    /**
     * 选择图片文件夹
     */
    fun imagePreviewSelect(bean: ImagePickerBean, position: Int)
}