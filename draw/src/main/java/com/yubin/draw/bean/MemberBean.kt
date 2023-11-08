package com.yubin.draw.bean

import android.net.Uri
import android.os.Bundle
import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData
import java.io.Serializable

data class MemberBean(
    val name: String? = null,
    val unitName: String? = null,
    val imageUri: String? = null,
) : Serializable, ICECDiffData {

    override fun getItemSameId(): String {
        return Uri.EMPTY.toString()
    }

    override fun getChangePayload(other: ICECDiffData?): Bundle? {
        return null
    }
}