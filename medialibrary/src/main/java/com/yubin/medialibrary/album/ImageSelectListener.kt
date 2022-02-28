package com.yubin.medialibrary.album

import com.yubin.medialibrary.album.been.ImagePickerBean

/**
 * description
 * @date create at 2019-07-13 11:02
 */
interface ImageSelectListener {
    fun onImageSelected(pickerBean: ImagePickerBean, isSelect: Boolean): Int
}