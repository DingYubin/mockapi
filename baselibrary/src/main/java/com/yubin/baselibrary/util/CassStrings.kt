package com.yubin.baselibrary.util

/**
 * 去掉所有空格
 */
fun String.removeSpace(): String {
    return this.replace(" ", "")
}