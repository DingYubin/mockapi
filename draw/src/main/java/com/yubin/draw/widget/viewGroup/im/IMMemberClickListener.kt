package com.yubin.draw.widget.viewGroup.im

import androidx.recyclerview.widget.RecyclerView
import com.yubin.draw.bean.MemberBean

interface IMMemberClickListener {
    /**
     * 成员点击
     */
    fun onItemClick(
        member: MemberBean,
        position: Int,
        adapter: RecyclerView.Adapter<*>?
    )
}