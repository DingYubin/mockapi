package com.yubin.draw.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.util.ResourceUtil.getString
import com.yubin.draw.R
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.widget.recyclerView.adapter.CECViewHolder
import com.yubin.draw.widget.viewGroup.im.IMMemberClickListener

class MemberAllSelectVH(itemView: View, private val memberClickListener: IMMemberClickListener) :
    CECViewHolder<MemberBean>(itemView) {

    private val tvMemberAll: AppCompatTextView = itemView.findViewById(R.id.im_member_all)
    private val mPersonalInfoLayout: ConstraintLayout =
        itemView.findViewById(R.id.im_person_all_layout)

    override fun bindDataFully(data: MemberBean, position: Int, count: Int) {
        tvMemberAll.text = String.format(getString(R.string.all_member), data.memberSize)
        mPersonalInfoLayout.onViewClick {
            memberClickListener.onItemClick(data, position, null)
        }
    }

    override fun bindDiffData(data: MemberBean, payload: Bundle, position: Int, count: Int) {
    }
}