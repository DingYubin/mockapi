package com.yubin.draw.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter

class QualityAdapter : CECAdapter<QualityBean, BaseQuotationViewHolder>() {

    /**
     * 主配件信息条目
     */
    companion object {
        const val VIEW_TYPE_MAIN = 0x1001
        const val VIEW_TYPE_CONTENT = 0x1002
    }

    override fun getItemViewType(position: Int): Int {
        val viewType: Int = data[position].itemViewType
        if (viewType == 0) {
            Log.e(javaClass.simpleName, "getItemViewType error: " + data[position].toString())
        }
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseQuotationViewHolder {

        return when (viewType) {
            VIEW_TYPE_MAIN ->
                QualityTitleHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.quality_title_item, parent, false)
                )
            VIEW_TYPE_CONTENT ->{
                return QualityViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.quality_item, parent, false)
                )
            }

            else -> throw IllegalStateException("Unexpected value: $viewType")
        }
    }

    override fun submitList(listData: List<QualityBean>) {
        super.submitList(listData)
    }

    override fun addList(listData: List<QualityBean>) {
        super.addList(listData)
    }

}