package com.yubin.draw.bean

import android.view.View
import com.yubin.baselibrary.util.CMUnitHelper.dp2px
import com.yubin.baselibrary.util.CECDeviceHelper
import com.yubin.baselibrary.util.CMUnitHelper
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.yubin.draw.R

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/03/16
 * desc    : 会话
 * version : 1.0
</pre> *
 */
class GoodsItemView internal constructor(itemView: View) {
    private val mItemView: View
    private val goodsPicFl: View
    private val goodsPicIv: AppCompatImageView
    private val priceTv: AppCompatTextView

    /**
     * 商品头图容器宽度
     */
    private val goodsPicItemH: Int
    private val showPriceFlag = 0

    init {
        goodsPicItemH =
            (CECDeviceHelper.screenWidthWithContext(itemView.context) - dp2px(itemView.context, 10f)
                .toInt() * 4) / 3
        mItemView = itemView
        goodsPicFl = itemView.findViewById(R.id.goodsPicFl)
        goodsPicIv = itemView.findViewById(R.id.goodsPicIv)
        priceTv = itemView.findViewById(R.id.priceTv)
        visibleAfterCertificationTv = itemView.findViewById<View>(R.id.visibleAfterCertificationTv)
        val layoutParams = goodsPicFl.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = goodsPicItemH
        layoutParams.width = goodsPicItemH
    }
}