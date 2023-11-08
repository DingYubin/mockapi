package com.yubin.draw.widget.viewGroup.im

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yubin.baselibrary.core.BaseApplication
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.util.CMUnitHelper
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.MockUtil
import com.yubin.draw.R
import com.yubin.draw.adapter.MemberAdapter
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.bean.MemberList
import com.yubin.draw.extend.DrawHelper
import com.yubin.draw.ui.ImActivity
import com.yubin.draw.widget.recyclerView.CMRecyclerView
import com.yubin.ecwindow.ECPopWindow

class ImAtGroupMemberView(context: Context) : ConstraintLayout(context) {

    private var ecPopWindow: ECPopWindow? = null
    private var mActivity: ImActivity? = null
    private var rvMemberList: CMRecyclerView? = null
    private var clContainer: ConstraintLayout? = null
    private var tvMemberNumber: AppCompatTextView? = null
    private var flContainer: FrameLayout? = null
    private var ivClose: AppCompatImageView? = null
    private var mAdapter: MemberAdapter? = null
    private var mDataList: MutableList<MemberBean> = mutableListOf()
    private val maxShowHeight: Int

    init {
        maxShowHeight =
            ((CMUnitHelper.getAppUsableScreenSize(context).y * 0.8) - CMUnitHelper.dp2px(100f)).toInt()
        mActivity = context as? ImActivity
        initView()
        initData()
    }

    private fun initView() {

        inflate(context, R.layout.inquiry_layout_at_group_member, this)
        rvMemberList = findViewById(R.id.cl_member_list)
        clContainer = findViewById(R.id.cl_area_container)
        flContainer = findViewById(R.id.fl_container)
        ivClose = findViewById(R.id.close)
        tvMemberNumber = findViewById(R.id.im_member_number)

        ivClose?.onViewClick {
            ecPopWindow?.dismiss()
        }

        mAdapter = MemberAdapter()
        rvMemberList?.layoutManager = LinearLayoutManager(context)
        rvMemberList?.adapter = mAdapter

    }

    private fun initData() {
        //mock 数据
        val mockJson: String = MockUtil.stringFromAssets(BaseApplication.context, "member.json")
        val data: MemberList = Gson().fromJson(mockJson, MemberList::class.java)
        data.members?.let { mDataList.addAll(it) }
        mAdapter?.submitListWithKeywords(mDataList, "")
        tvMemberNumber?.text = String.format(context.getString(R.string.all_member), mDataList.size)
        LogUtil.d("data: $mDataList")
    }

    /**
     * 显示成员列表
     */
    fun showImAtGroupMemberWindow(view: View) {
        if (null == ecPopWindow) {
            ecPopWindow = ECPopWindow.Builder.build(context)
                .setContentView(this)
                .setSize(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                .createWindow()
        }
        ecPopWindow?.isClippingEnabled = false
        ecPopWindow?.showInScreenLocation(
            view,
            Gravity.BOTTOM,
            0,
            DrawHelper.getNavigationBarHeight(mActivity)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if ((rvMemberList?.measuredHeight ?: 0) > maxShowHeight) {
            val lp = rvMemberList?.layoutParams
            lp?.height = maxShowHeight
            rvMemberList?.layoutParams = lp
        }
    }
}