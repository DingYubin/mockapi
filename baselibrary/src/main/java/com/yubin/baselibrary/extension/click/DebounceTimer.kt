package com.yubin.baselibrary.extension.click

object DebounceTimer {
    var enabled = true
    val ENABLE_AGAIN = Runnable { enabled = true }
}