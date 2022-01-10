package com.yubin.mvvm.net.dsl

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Description:  请求Body的 DSL
 *
 */
class BodyMap {

    val jsonObject = JsonObject()

    infix fun String.with(that: Number?) {
        that ?: return
        this.apply {
            jsonObject.addProperty(this, that)
        }
    }

    infix fun String.with(that: Boolean?) {
        that ?: return
        this.apply {
            jsonObject.addProperty(this, that)
        }
    }

    infix fun String.with(that: Char?) {
        that ?: return
        this.apply {
            jsonObject.addProperty(this, that)
        }
    }

    infix fun String.with(that: String?) {
        that ?: return
        if (that.isBlank()) {
            return
        }
        this.apply {
            jsonObject.addProperty(this, that)
        }
    }

    infix fun String.with(that: JsonElement?) {
        that ?: return
        this.apply {
            jsonObject.add(this, that)
        }
    }

    infix fun String.with(that: Any?) {
        that ?: return
        this.apply {
            jsonObject.add(this, Gson().toJsonTree(that))
        }
    }

    infix fun String.withArray(that: Array<out String?>?) {
        that ?: return
        this.apply {
            val jsonArray = JsonArray()
            that.forEach {
                it ?: return@forEach
                jsonArray.add(it)
            }
            jsonObject.add(this, jsonArray)
        }
    }

    infix fun String.withArray(that: Array<out Char?>?) {
        that ?: return
        this.apply {
            val jsonArray = JsonArray()
            that.forEach {
                it ?: return@forEach
                jsonArray.add(it)
            }
            jsonObject.add(this, jsonArray)
        }
    }

    infix fun String.withArray(that: Array<out Number?>?) {
        that ?: return
        this.apply {
            val jsonArray = JsonArray()
            that.forEach {
                it ?: return@forEach
                jsonArray.add(it)
            }
            jsonObject.add(this, jsonArray)
        }
    }

    infix fun String.withArray(that: Array<out Boolean?>?) {
        that ?: return
        this.apply {
            val jsonArray = JsonArray()
            that.forEach {
                it ?: return@forEach
                jsonArray.add(it)
            }
            jsonObject.add(this, jsonArray)
        }
    }

    infix fun String.withArray(that: JsonArray?) {
        that ?: return
        this.apply {
            jsonObject.add(this, that)
        }
    }

}

/**
 *  构建一个Json字符串，如：
 *  body {
 *    "userId" with  userName
 *    "password" with password
 *  }
 *
 */
fun body(block: BodyMap.() -> Unit): String {
    return BodyMap().let {
        block(it)
        it.jsonObject.toString()
    }
}

fun query(block: BodyMap.() -> Unit): String {
    return body(block)
}

fun requestBody(block: BodyMap.() -> Unit): RequestBody {
    return BodyMap().let {
        block(it)
        it.jsonObject.toString()
    }.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

