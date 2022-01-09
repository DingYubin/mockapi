package com.yubin.baselibrary.function

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * description: Kotlin顶级函数集合
 */

/**
 * 协程相关
 */
object CMCoroutineFunction{

    /**
     * 在协程内执行suspend方法
     * @param context 协程上下文
     * @param start 协程启动模式
     * @param exceptionHandler 协程异常处理
     * @param block 需要执行的函数
     */
    fun suspendCoroutine(
        context: CoroutineContext = Dispatchers.Default,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        exceptionHandler: CoroutineExceptionHandler,
        block: suspend CoroutineScope.() -> Unit,
    ) = CoroutineScope(context = context)
        .launch(
            context = exceptionHandler,
            start = start,
            block = block
        )
}
