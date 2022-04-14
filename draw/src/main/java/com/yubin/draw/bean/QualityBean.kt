package com.yubin.draw.bean

import android.net.Uri
import android.os.Bundle
import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData
import java.io.Serializable

data class QualityBean(
//    val categoryCode: String,
    val index: Int
//    var qualities: List<Quality>,
//    val qualityType: String,
//    var isShowDefault: Boolean?,
//    var originInfo: String?,
//    var needId: String?
) : Serializable, ICECDiffData {

    override fun getItemSameId(): String {
        return Uri.EMPTY.toString()
    }

    override fun getChangePayload(other: ICECDiffData?): Bundle? {
        return null
    }
}

data class Quality(
    var checked: Boolean,
    val qualityId: String,
    val qualityName: String,
    var isDisable: Boolean?
) : Serializable, ICECDiffData {

    override fun getItemSameId(): String {
        return Uri.EMPTY.toString()
    }

    override fun getChangePayload(other: ICECDiffData?): Bundle? {
        return null
    }
}