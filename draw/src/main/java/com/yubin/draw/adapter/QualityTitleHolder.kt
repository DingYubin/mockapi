package com.yubin.draw.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean

class QualityTitleHolder(itemView: View) : BaseQuotationViewHolder(itemView) {

    //在布局文件中使用ExposureLayout作为父布局
    private val title: AppCompatTextView = itemView.findViewById(R.id.title)

    @SuppressLint("SetTextI18n")
    override fun bindDataFully(data: QualityBean, position: Int, count: Int) {
        title.text = "间隔"+ data.index.toString()
    }


    override fun bindDiffData(data: QualityBean, payload: Bundle, position: Int, count: Int) {
    }

}