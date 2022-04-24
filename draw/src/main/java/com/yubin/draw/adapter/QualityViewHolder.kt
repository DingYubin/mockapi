package com.yubin.draw.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.collection.ArrayMap
import com.alibaba.android.arouter.launcher.ARouter
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.widget.viewGroup.exposure.ExposureLayout
import com.yubin.draw.widget.viewGroup.exposure.ExposureManager

class QualityViewHolder(itemView: View) : BaseQuotationViewHolder(itemView) {

    //在布局文件中使用ExposureLayout作为父布局
    private val exposureLayout: ExposureLayout = itemView.findViewById(R.id.layout_exposure)

    private val num: AppCompatTextView = itemView.findViewById(R.id.num)


    override fun bindDataFully(data: QualityBean, position: Int, count: Int) {

        num.text = data.index.toString()

        if (data.dataId == "event_15") {
            LogUtil.d("ExposureHandler position = $position, num = ${data.index}, dataId = ${data.dataId}")
            exposureLayout.run {
                setPage("exposure_activity")
                setShowRatio(0.5f) //需要暴露大于50%才能曝光
                setTimeLimit(2000) //曝光的时间2000
                val map = ArrayMap<String, Any>()
                map["eventId"] = data.dataId
                map["isExpose"] = true
                bindViewData(map)
            }

            ExposureManager.instance.addEvent(data.dataId)
            num.setOnClickListener {
                ARouter.getInstance()
                    .build(RouterPath.UiPage.PATH_UI_CALLBACK)
                    .navigation()
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