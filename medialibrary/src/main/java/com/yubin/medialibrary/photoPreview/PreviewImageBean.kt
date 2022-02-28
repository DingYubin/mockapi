package com.yubin.medialibrary.photoPreview

import android.os.Bundle
import com.yubin.medialibrary.extra.IDiffData

/**
 * @description:
 * <p>
 * @author: 孙莹莹(A01266)
 * <p>
 * @date: 2021/3/1 11:42 AM
 */
data class PreviewImageBean(var url: String, var isSelect: Boolean, var isVideo: Boolean) :
    IDiffData {
    override fun getItemSameId(): String {
        return ""
    }

    override fun getChangePayload(other: IDiffData?): Bundle? {
        return null
    }
}