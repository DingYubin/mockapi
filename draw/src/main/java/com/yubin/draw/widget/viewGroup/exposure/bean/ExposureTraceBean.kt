package com.yubin.draw.widget.viewGroup.exposure.bean

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData
import java.io.Serializable

data class ExposureTraceBean(
    val page: String,
    val exposureId: String,//曝光id
    val view: View,
    var timeStep: Int, //时间步长
    val area: Float,
    val mTimeLimit: Int,
    var isExposeAble: Boolean = true //默认能曝光
) : Serializable, ICECDiffData {

    override fun getItemSameId(): String {
        return Uri.EMPTY.toString()
    }

    override fun getChangePayload(other: ICECDiffData?): Bundle? {
        return null
    }
}