package com.yubin.draw.widget.view

import android.text.Spanned
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.yubin.baselibrary.core.BaseApplication
import com.yubin.baselibrary.util.CECDeviceHelper
import com.yubin.baselibrary.util.CMUnitHelper.dp2px
import com.yubin.baselibrary.util.EmptyUtil
import com.yubin.baselibrary.util.HidePriceHelper
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.bean.StoreList
import java.util.Locale

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/03/16
 * desc    : 会话
 * version : 1.0
 * internal 关键字限制了跨 module 的方法的使用
</pre> *
 */
class GoodsItemView internal constructor(itemView: View) {
    private val mItemView: View = itemView
    private val goodsPicFl: View = itemView.findViewById(R.id.goodsPicFl)
    private val goodsPicIv: AppCompatImageView = itemView.findViewById(R.id.goodsPicIv)
    private val priceTv: AppCompatTextView = itemView.findViewById(R.id.priceTv)
    private val visibleAfterCertificationTv: AppCompatTextView = itemView.findViewById<View>(R.id.visibleAfterCertificationTv) as AppCompatTextView

    /**
     * 商品头图容器宽度
     */
    private val goodsPicItemH: Int =
        (CECDeviceHelper.screenWidthWithContext(itemView.context) - dp2px(10f)
            .toInt() * 4) / 3
    private var showPriceFlag = 0

    init {
        val layoutParams = goodsPicFl.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = goodsPicItemH
        layoutParams.width = goodsPicItemH
    }

    fun setData(goodsInfo: StoreList.StoreListBean.ProductsBean?, showPriceFlag: Int) {
        mItemView.visibility = View.VISIBLE
        mItemView.setOnClickListener {
            LogUtil.d("跳转商品详情页面")
        }
        Glide.with(BaseApplication.context)
            .load(goodsInfo?.imageUri)
            .into(goodsPicIv)
        this.showPriceFlag = showPriceFlag
        priceTv.text = goodsInfo?.price?.let { setPrice(it) }
    }

    private fun setPrice(price: String): Spanned? {
        var prices = price.trim { it <= ' ' }.split("\\.").toTypedArray()
        if (showPriceFlag > 0) {
            if (prices.size == 1) {
                prices = arrayOf(prices[0], "00")
            }
            if (EmptyUtil.isStringNotEmpty(prices[0]) && prices[0].length > 0) {
                prices[0] = prices[0].replace(",".toRegex(), "")
                val temp = StringBuilder()
                for (i in 0 until prices[0].length) {
                    temp.append("?")
                }
                prices[0] = temp.toString()
            }
            if (showPriceFlag == 2) {
                visibleAfterCertificationTv.visibility = View.VISIBLE
            } else if (showPriceFlag == 1) {
                visibleAfterCertificationTv.visibility = View.GONE
            }
        }
        val rmbSymbol: String = HidePriceHelper.Companion.rmbSymbol
        if (prices.size != 2) {
            return HtmlCompat.fromHtml(
                String.format("%s%s", rmbSymbol, price),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        val pricePattern = "%s%s<font><small>.%s</small></font>"
        return HtmlCompat.fromHtml(
            String.format(
                Locale.getDefault(), pricePattern, rmbSymbol,
                prices[0],
                prices[1]
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}