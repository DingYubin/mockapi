package com.yubin.draw.bean

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData
import java.io.Serializable

data class ExposureViewTraceBean(
    val eventId: String,
    val view: View,
    var time: Int,
    val area: Float,
    val mTimeLimit: Int,
    var isExpose: Boolean = true //默认需要曝光
) : Serializable, ICECDiffData {

    override fun getItemSameId(): String {
        return Uri.EMPTY.toString()
    }

    override fun getChangePayload(other: ICECDiffData?): Bundle? {
        return null
    }
}