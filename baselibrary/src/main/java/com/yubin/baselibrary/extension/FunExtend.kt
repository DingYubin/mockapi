package com.yubin.baselibrary.extension

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.yubin.baselibrary.extension.click.DebounceTimer

/**
 * 扩展函数集合
 */

/**
 * 防止重复点击扩展函数
 */
fun View.onViewClick(click: View.OnClickListener) {
    setOnClickListener(View.OnClickListener {
        if (!DebounceTimer.enabled) {
            return@OnClickListener
        }
        DebounceTimer.enabled = false
        it.postDelayed(DebounceTimer.ENABLE_AGAIN, 200)
        click.onClick(it)
    })
}

/**
 * 通知监听刷新
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.postValue(this.value)
}