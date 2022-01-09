package com.yubin.net.model

/**
 * Description: 网络请求错误时服务器返回的Response Body
 */
data class ErrorResponseBody(var statusCode: Int,
                             var error: String?=null,
                             var message: String?=null,
                             var errorCode: String?=null,
                             var errState: String?=null,
                             var errorMsg: String?=null)
