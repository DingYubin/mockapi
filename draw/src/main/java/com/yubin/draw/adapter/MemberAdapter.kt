package com.yubin.draw.adapter

import android.view.ViewGroup
import com.yubin.draw.R
import com.yubin.draw.bean.KeywordsValue
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter

class MemberAdapter : CECAdapter<MemberBean, MemberSelectVH>() {
    var keywordsValue: KeywordsValue = KeywordsValue()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MemberSelectVH {
        val itemView =
            getLayoutInflater(p0).inflate(R.layout.im_item_select_chat, p0, false)
        return MemberSelectVH(itemView, keywordsValue)
    }

    fun submitListWithKeywords(listData: List<MemberBean>, keywords: String) {
        this.keywordsValue.keywords = keywords
        submitList(listData)
    }
}