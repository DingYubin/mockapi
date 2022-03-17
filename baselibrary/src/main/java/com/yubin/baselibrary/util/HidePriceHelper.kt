package com.casstime.base.util

import java.lang.StringBuilder

/**
 * description:
 * <p>
 * email: chuhuan.yan@casstime.com
 * <p>
 * date: created on 2021/12/9:17:27
 * <p>
 * author: chuhuan.yan(A03520)
 */
class CECHidePriceHelper {

    companion object {

        /**
         * 人民币符号
         */
        val rmbSymbol = 165.toChar().toString()

        /**
         * 屏蔽金额整数部分
         * 如123.23 -> ???.23
         */
        fun hidePrice(price: Float): String {
            val temp = String.format("%.2f", price).split(".")
            if (EmptyUtil.isCollectionNotEmpty(temp) && temp.size > 1) {
                var str = StringBuilder()
                temp.get(0).length.let {
                    if (it > 0) {
                        for (i in 0..(it - 1)) {
                            str.append("?")
                        }
                    }
                }
                str.append(".")
                str.append(temp.get(1))
                return str.toString()
            }
            return "?.00"
        }
    }
}