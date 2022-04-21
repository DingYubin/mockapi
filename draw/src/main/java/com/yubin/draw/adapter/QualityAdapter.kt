package com.yubin.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.collection.ArrayMap
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter

class QualityAdapter : CECAdapter<QualityBean, QualityViewHolder>() {

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.exposureLayout.run {
            setPage("exposure_activity")
            setShowRatio(0.5f) //需要暴露大于50%才能曝光
            setTimeLimit(2000) //曝光的时间2000
            val map = ArrayMap<String, String>()
            map["eventId"] = "exposure_$position"
            bindViewData(map)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        return QualityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quality_item, parent, false)
        )
    }

}