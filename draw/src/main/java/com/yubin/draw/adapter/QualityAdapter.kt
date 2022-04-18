package com.yubin.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter
import com.yubin.draw.widget.viewGroup.exposure.IExposureCallback

class QualityAdapter : CECAdapter<QualityBean, QualityViewHolder>() {

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.exposureLayout.run {
            setShowRatio(0.5f) //需要暴露大于50%才能曝光
            setTimeLimit(2000) //需要显示时长超过两秒才能曝光
            setExposureCallback(object : IExposureCallback {
                override fun show() {
                    //曝光
                    LogUtil.i("第 ${holder.num.text} 曝光")
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        return QualityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quality_item, parent, false)
        )
    }

}