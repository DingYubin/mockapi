package com.yubin.baselibrary.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yubin.baselibrary.function.CMCoroutineFunction
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Description: AndroidViewModel基类
 */
open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    /**
     * 网络状态监听
     */
    val networkStatus: LiveData<NetworkState>
        get() = _networkStatus

    /**
     * 网络状态
     */
    protected val _networkStatus = MutableLiveData<NetworkState>()

    //在协程内执行suspend方法
    fun suspendCoroutine(
        context: CoroutineContext = Dispatchers.Default,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        exceptionHandler: CoroutineExceptionHandler = defaultExceptionHandler,
        block: suspend CoroutineScope.() -> Unit,
    ) = CMCoroutineFunction.suspendCoroutine(
        context,
        start,
        exceptionHandler,
        block
    )

    //默认异常处理，需要在自定义拦截器内抛出异常，在这里捕获
    private val defaultExceptionHandler =
        CoroutineExceptionHandler { context, throwable ->
            //处理异常
            _networkStatus.postValue(NetworkState.EXCEPTION)
            //debug模式才显示toast
//            if (BuildConfig.DEBUG) {
//                throwable.message?.toast()
//            }
            Log.e(
                "BaseViewModel",
                "handleException() called with: context = $context, throwable = $throwable"
            )
        }
}