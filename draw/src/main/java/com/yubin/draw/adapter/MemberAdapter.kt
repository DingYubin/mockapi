package com.yubin.draw.adapter

import android.util.Log
import android.view.ViewGroup
import com.yubin.draw.R
import com.yubin.draw.bean.KeywordsValue
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.widget.recyclerView.adapter.CECAdapter
import com.yubin.draw.widget.recyclerView.adapter.CECViewHolder
import com.yubin.draw.widget.viewGroup.im.IMMemberClickListener

const val VIEW_TYPE_ALL = 0x1001
const val VIEW_TYPE_MEMBER = 0x1002

class MemberAdapter(private val memberClickListener: IMMemberClickListener) :
    CECAdapter<MemberBean, CECViewHolder<MemberBean>>() {
    private var keywordsValue: KeywordsValue = KeywordsValue()

    override fun getItemViewType(position: Int): Int {
        val viewType: Int = data[position].itemViewType ?: 0
        if (viewType == 0) {
            Log.e(javaClass.simpleName, "getItemViewType error: " + data[position].toString())
        }
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CECViewHolder<MemberBean> {

        return when (viewType) {
            VIEW_TYPE_ALL -> {
                MemberAllSelectVH(
                    getLayoutInflater(parent).inflate(
                        R.layout.im_item_member_all,
                        parent,
                        false
                    ), memberClickListener
                )
            }

            VIEW_TYPE_MEMBER -> {
                MemberSelectVH(
                    getLayoutInflater(parent).inflate(
                        R.layout.im_item_member,
                        parent,
                        false
                    ), keywordsValue, memberClickListener
                )
            }

            else -> throw IllegalStateException("Unexpected value: $viewType")
        }
    }

    fun submitListWithKeywords(listData: List<MemberBean>, keywords: String) {
        this.keywordsValue.keywords = keywords
        submitList(listData)
    }
}