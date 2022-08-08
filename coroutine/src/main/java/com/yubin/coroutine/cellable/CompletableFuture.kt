package com.yubin.coroutine.cellable

import com.yubin.coroutine.utils.log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resumeWithException

suspend fun main() {
   val result = CompletableFuture.supplyAsync{
        3
    }.await()
    log(3)
}

suspend fun <T> CompletableFuture<T>.await(): T {
    if (isDone){
        try {
            return get()
        } catch (e: Exception) {
            throw e.cause ?: e
        }
    }

    return suspendCancellableCoroutine {
        cancellableContinuation ->
        cancellableContinuation.invokeOnCancellation {
            cancel(true)
        }

        whenComplete { value, throwable ->
            if (throwable == null){
                cancellableContinuation.resume(value, throwable)
            }else{
                cancellableContinuation.resumeWithException(throwable.cause ?: throwable)
            }
        }
    }


}