package com.yubin.draw.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.yubin.baselibrary.viewmodel.BaseViewModel
import com.yubin.draw.bean.CECIMSearchChat
import com.yubin.draw.bean.MemberBean

class ImMemberViewModel(app: Application) : BaseViewModel(app) {

    var mSearchResultLD = MutableLiveData<CECIMSearchChat>()

    fun getSearchContactsList(keywords: String, mDataList: List<MemberBean>) {
        mSearchResultLD.value =
            CECIMSearchChat(keywords, mDataList.filter { it.name?.contains(keywords) ?: false })
    }
}