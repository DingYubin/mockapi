package com.yubin.draw.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.recyclerView.adapter.CECViewHolder

class QualityViewHolder(itemView: View) : CECViewHolder<QualityBean>(itemView) {

    override fun bindDataFully(data: QualityBean, position: Int, count: Int) {

        val num : AppCompatTextView = itemView.findViewById(R.id.num)
        num.text = data.index.toString()

        when {
            data.index % 3 == 0 -> {
                num.setBackgroundResource(R.color.text_common_stock_red)
            }
            data.index % 3 == 1 -> {
                num.setBackgroundResource(R.color.st_bg_common_master_blue)
            }
            data.index % 3 == 2 -> {
                num.setBackgroundResource(R.color.st_portfolio_switch_down_color)
            }
        }
    }



    override fun bindDiffData(data: QualityBean, payload: Bundle, position: Int, count: Int) {
    }

}