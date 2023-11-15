package com.yubin.draw.bean

import java.io.Serializable


/**
 * 非at消息
 */
const val AT_TYPE_NO: Int = 0

/**
 * 所有人
 */
const val AT_TYPE_ALL: Int = 1

/**
 * 单人at消息
 */
const val AT_TYPE_SINGLE: Int = 2

/**
 * At 消息信息
 */
data class CECIMentionedInfo(

    var type: Int? = null,
    var imId: String? = null,
    var mentionedContent: String? = null,

) : Serializable
