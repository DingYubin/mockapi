package com.yubin.draw.widget.viewGroup.im

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.util.CMUnitHelper
import com.yubin.draw.R
import com.yubin.draw.adapter.MemberAdapter
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.extend.DrawHelper
import com.yubin.draw.extend.QuotationWindowHelper
import com.yubin.draw.model.ImMemberViewModel
import com.yubin.draw.ui.TabActivity
import com.yubin.draw.widget.recyclerView.CMRecyclerView
import com.yubin.ecwindow.ECPopWindow

class ImTabGroupMemberView(context: Context) : ConstraintLayout(context), IMMemberClickListener {

    private var ecPopWindow: ECPopWindow? = null
    private var mActivity: TabActivity? = null
    private var rvMemberList: CMRecyclerView? = null
    private var clContainer: ConstraintLayout? = null
    private var flContainer: FrameLayout? = null
    private var ivClose: AppCompatImageView? = null
    private var mKeywordsBar: ImKeywordsSearch? = null
    private var mViewModel: ImMemberViewModel? = null
    private var title: AppCompatTextView? = null
    private val mAdapter by lazy { MemberAdapter(this)}
    private val maxShowHeight: Int = ((CMUnitHelper.getAppUsableScreenSize(context).y * 0.8) - CMUnitHelper.dp2px(100f)).toInt()

    private var mSelectedMemberListener: ((member: MemberBean) -> Unit)? = null

    init {
        mActivity = context as? TabActivity
        initView()
        initViewModel()
    }

    private fun initView() {

        inflate(context, R.layout.im_layout_tab_group_member, this)
        title = findViewById(R.id.group_title)
        mKeywordsBar = findViewById(R.id.im_keywords_bar)
        rvMemberList = findViewById(R.id.cl_member_list)
        clContainer = findViewById(R.id.cl_area_container)
        flContainer = findViewById(R.id.fl_container)
        ivClose = findViewById(R.id.close)
        rvMemberList?.layoutManager = LinearLayoutManager(context)
        rvMemberList?.adapter = mAdapter

        flContainer?.onViewClick {
            ecPopWindow?.dismiss()
            mViewModel = null
        }

        ivClose?.onViewClick {
            ecPopWindow?.dismiss()
            mViewModel = null
        }

        title?.onViewClick {
            QuotationWindowHelper.showQuotationCouponWindow(
                context,
                title,
                "选择提醒的成员"
            )
        }

        mKeywordsBar?.setKeywordsChangedListener { keywords ->
            if (TextUtils.isEmpty(keywords)) {
                mViewModel?.mDataList?.let { mAdapter.submitListWithKeywords(it, keywords) }
            } else {
                fetchSearchContacts(keywords)
            }
        }
    }

    private fun fetchSearchContacts(keywords: String) {
        mViewModel?.mDataList?.let { mViewModel?.getSearchContactsList(keywords, it) }
    }

    private fun initViewModel() {
        if (mActivity == null) return

        if (mViewModel == null) {
            mViewModel = ViewModelProvider(mActivity!!)[ImMemberViewModel::class.java]
        }

        mViewModel?.mSearchResultLD?.observe(mActivity!!) {
            mAdapter.submitListWithKeywords(it.list, it.keywords ?: "")
        }
    }

    fun setData() {
        //mock 数据
        mViewModel?.getMemberList()
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
                ).setTouchOutsideDismiss(true)
                .createWindow()
        }
        ecPopWindow?.isClippingEnabled = false
        ecPopWindow?.showInScreenLocation(
            view,
            Gravity.END,
            0 ,
            DrawHelper.getNavigationBarHeight(mActivity)
        )
//        ecPopWindow?.showInScreenLocation(
//            view,
//            Gravity.BOTTOM,
//            0,
//            DrawHelper.getNavigationBarHeight(mActivity)
//        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if ((rvMemberList?.measuredHeight ?: 0) > maxShowHeight) {
//            val lp = rvMemberList?.layoutParams
//            lp?.height = maxShowHeight
//            rvMemberList?.layoutParams = lp
//        }
    }

    fun hideImAtGroupMemberWindow() {
        if (ecPopWindow?.isShowing == true) {
            ecPopWindow?.dismiss()
        }
    }

    override fun onItemClick(member: MemberBean, position: Int, adapter: RecyclerView.Adapter<*>?) {
        mSelectedMemberListener?.invoke(member)
    }

    fun selectMemberListener(listener: (str: MemberBean) -> Unit) {
        this.mSelectedMemberListener = listener
    }

}