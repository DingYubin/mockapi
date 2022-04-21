package com.yubin.draw.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.collection.ArrayMap
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.viewGroup.exposure.ExposureLayout

class QualityViewHolder(itemView: View) : BaseQuotationViewHolder(itemView) {

    //在布局文件中使用ExposureLayout作为父布局
    private val exposureLayout: ExposureLayout = itemView.findViewById(R.id.layout_exposure)

    private val num: AppCompatTextView = itemView.findViewById(R.id.num)

    override fun bindDataFully(data: QualityBean, position: Int, count: Int) {

        num.text = data.index.toString()
        LogUtil.d("ExposureHandler position = $position" )
        exposureLayout.run {
            if(position == 4) {
                setPage("exposure_activity")
                setShowRatio(0.5f) //需要暴露大于50%才能曝光
                setTimeLimit(2000) //曝光的时间2000
                val map = ArrayMap<String, String>()
                map["eventId"] = "exposure_price"
                bindViewData(map)
            } else {
                setPage("exposure_activity")
                setShowRatio(0.5f) //需要暴露大于50%才能曝光
                setTimeLimit(2000) //曝光的时间2000
                val map = ArrayMap<String, String>()
                map["eventId"] = "exposure"
                bindViewData(map)
            }
        }

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