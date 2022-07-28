package com.yubin.rxlibrary.bean

import com.google.gson.JsonObject
import java.io.Serializable


data class UserEntity(
    var accessToken: String? = null,
    var userLoginId: String? = null,
    var isLogin: Boolean = false
) : Serializable {
    override fun toString(): String {
        val jsonObject = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("userLoginId", userLoginId)
            addProperty("isLogin", isLogin)
        }
        return jsonObject.toString()
    }
}

