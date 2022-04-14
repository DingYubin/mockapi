package com.yubin.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter

class QualityAdapter : CECAdapter<QualityBean, QualityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        return QualityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quality_item, parent, false)
        )
    }

}