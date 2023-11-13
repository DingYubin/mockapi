package com.yubin.draw.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.yubin.baselibrary.core.BaseApplication
import com.yubin.baselibrary.util.MockUtil
import com.yubin.baselibrary.viewmodel.BaseViewModel
import com.yubin.draw.adapter.VIEW_TYPE_ALL
import com.yubin.draw.adapter.VIEW_TYPE_MEMBER
import com.yubin.draw.bean.CECIMSearchChat
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.bean.MemberList

class ImMemberViewModel(app: Application) : BaseViewModel(app) {

    var mDataList: MutableList<MemberBean> = mutableListOf()

    var mSearchResultLD = MutableLiveData<CECIMSearchChat>()

    fun getSearchContactsList(keywords: String, mDataList: List<MemberBean>) {
        val keywords = keywords.trim { it <= ' ' }
        mSearchResultLD.postValue(
            CECIMSearchChat(
                keywords,
                mDataList.filter { member ->
                    if (keywords.isEmpty()) {
                        member.itemViewType == VIEW_TYPE_MEMBER
                    } else {
                        member.name?.contains(keywords) ?: false
                    }
                })
        )
    }

    fun getMemberList() {
        mDataList.clear()
        val mockJson: String = MockUtil.stringFromAssets(BaseApplication.context, "member.json")
        val data: MemberList = Gson().fromJson(mockJson, MemberList::class.java)
        if (data.members?.isNotEmpty() == true) {
//            mDataList.add(MemberBean())
            mDataList.addAll(data.members)
            mDataList.add(0, MemberBean())
            mDataList.forEachIndexed { index, it ->
                if (index == 0) {
                    it.name = "所有人"
                    it.itemViewType = VIEW_TYPE_ALL
                    it.memberSize = mDataList.size - 1
                } else {
                    it.itemViewType = VIEW_TYPE_MEMBER
                }
            }
            mSearchResultLD.postValue(
                CECIMSearchChat(
                    "",
                    mDataList
                )
            )
        }
    }
}