package com.yubin.draw.bean

data class CECIMSearchChat(
    var keywords: String? = null,
    var list: List<MemberBean> = arrayListOf()
)