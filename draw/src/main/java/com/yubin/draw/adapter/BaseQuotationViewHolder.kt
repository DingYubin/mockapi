package com.yubin.draw.adapter

import android.os.Bundle
import android.view.View
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECViewHolder

/**
 * description
 */
open class BaseQuotationViewHolder(itemView: View) : CECViewHolder<QualityBean>(itemView) {

    override fun bindDataFully(data: QualityBean, position: Int, count: Int) {
        //parent class do nothing
    }

    override fun bindDiffData(data: QualityBean, payload: Bundle, position: Int, count: Int) {
        //parent class do nothing
    }
}