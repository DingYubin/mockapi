package com.yubin.baselibrary.util

/**
 * description:
 */
class HidePriceHelper {

    companion object {

        /**
         * 人民币符号
         */
        const val rmbSymbol = 165.toChar().toString()

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
                        for (i in 0 until it) {
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