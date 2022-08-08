@file:JvmName("CancellableCoroutine")
package com.yubin.coroutine.cellable

import com.yubin.coroutine.api.gitHubApi
import com.yubin.coroutine.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.await():T = suspendCancellableCoroutine {
    continuation ->
    continuation.invokeOnCancellation {
        cancel()
    }

    enqueue(object : Callback<T>{
        override fun onResponse(call: Call<T>, response: Response<T>) {
            response.takeIf {it.isSuccessful}?.body()?.also { continuation.resume(it) }
                ?:continuation.resumeWithException(HttpException(response))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            continuation.resumeWithException(t)
        }
    })
}

suspend fun main() {
//    val user = gitHubApi.getUserCallback("dingyubin").await()
//    log(user)
    /**
     * 挂起支持取消
     */
    GlobalScope.launch{
        val user = gitHubApi.getUserCallback("dingyubin").await()
        log(user)
    }.cancelAndJoin()

}