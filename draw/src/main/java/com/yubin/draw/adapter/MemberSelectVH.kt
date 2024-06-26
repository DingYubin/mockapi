package com.yubin.draw.adapter

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.image.CECImageUrlView
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.draw.R
import com.yubin.draw.bean.KeywordsValue
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.widget.recyclerView.adapter.CECViewHolder
import com.yubin.draw.widget.viewGroup.im.IMMemberClickListener

class MemberSelectVH(
    itemView: View,
    private var keywordsValue: KeywordsValue = KeywordsValue(),
    private val memberClickListener: IMMemberClickListener
) :
    CECViewHolder<MemberBean>(itemView) {

    private val mNameTv: AppCompatTextView = itemView.findViewById(R.id.im_chat_name)
    private val mUnitTv: AppCompatTextView = itemView.findViewById(R.id.im_unit_name)
    private val mPortraitImg: CECImageUrlView = itemView.findViewById(R.id.im_person_avatar)
    private val mPersonalInfoLayout: ConstraintLayout =
        itemView.findViewById(R.id.im_person_info_layout)

    override fun bindDataFully(data: MemberBean, position: Int, count: Int) {
        mNameTv.text = formatDisplayName(keywordsValue.keywords, data.name ?: "")
        mUnitTv.text = data.unitName ?: ""
        mPortraitImg.setImageUrl(data.imageUri)
        mPersonalInfoLayout.onViewClick {
            memberClickListener.onItemClick(data, position, null)
        }
//        mPersonalInfoLayout.setOnClickListener {
//            LogUtil.i("选择 member: $data")
//            val map = ArrayMap<String, MemberBean>()
//            map["data"] = data
//            val event = CECEvent(CECIMConstants.EVENT_IM_SELECT_MEMBER, map)
//            CECEventBusHelper.post(event)
//        }
    }

    override fun bindDiffData(data: MemberBean, payload: Bundle, position: Int, count: Int) {
    }

    private fun formatDisplayName(keywords: String, displayName: String): CharSequence {
        if (TextUtils.isEmpty(keywords)) {
            return displayName
        }
        val kIndex = displayName.indexOf(keywords)
        if (kIndex < 0) {
            return displayName
        }
        val ss = SpannableString(displayName)
        ss.setSpan(
            ForegroundColorSpan(ResourceUtil.getColor(R.color.st_bg_common_deep_blue)),
            kIndex,
            kIndex + keywords.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return ss
    }
}