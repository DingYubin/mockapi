package com.yubin.net

/**
 * Description ：网络参数配置
 */
object NetworkConstants {

    /**
     * 加密
     */
    const val X_LOCAL_ENCRYPT = "local-encrypt"
    const val X_LOCAL_DECRYPT = "local-decrypt"
    const val X_CONTENT = "Content-Type"
    const val ENCRYPT_AVER = "text/encrypted;aver=1"

    const val ENCRYPT_USER = "1"

    const val ENCRYPT_HEADER_USER_QUERY = "$X_LOCAL_ENCRYPT:$ENCRYPT_USER"

}