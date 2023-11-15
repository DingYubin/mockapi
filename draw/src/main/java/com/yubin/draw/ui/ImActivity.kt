package com.yubin.draw.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.event.CECEvent
import com.yubin.baselibrary.event.CECEventBusHelper
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CECIMConstants
import com.yubin.baselibrary.util.KeyBoardHelper
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.bean.AT_TYPE_ALL
import com.yubin.draw.bean.AT_TYPE_NO
import com.yubin.draw.bean.AT_TYPE_SINGLE
import com.yubin.draw.bean.CECIMentionedInfo
import com.yubin.draw.bean.MemberBean
import com.yubin.draw.databinding.ActivityUiImBinding
import com.yubin.draw.widget.view.text.RemindHandler
import com.yubin.draw.widget.viewGroup.im.ImAtGroupMemberView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/04/14
 * desc    : im 视图
 * version : 1.0
</pre> *
 */
@Route(path = RouterPath.UiPage.PATH_UI_IM)
class ImActivity : NativeActivity<ActivityUiImBinding>() {

    private var atPopWindow: ImAtGroupMemberView? = null
    private var remindHandler: RemindHandler? = null
    private val mTempGroupMembers: MutableList<MemberBean> = mutableListOf()

    override fun getViewBinding(): ActivityUiImBinding =
        ActivityUiImBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.im_style)
        supportActionBar?.hide()
        CECEventBusHelper.register(this)
        initView()
    }

    private fun initView() {
        handleAtOptions()
        binding.send.onViewClick {
            sentText()
            binding.etInput.setText("")
        }
    }

    /**
     * 发送消息
     */
    private fun sentText() {
        val info: ArrayList<CECIMentionedInfo> = arrayListOf()
        val editable = binding.etInput.editableText
        val spans: Array<RemindHandler.RemindDynamicDrawableSpan>? = remindHandler?.reminds
        spans?.forEachIndexed { index, span ->
            LogUtil.d("spans : uid : ${span.uid}, nackname : ${span.nickname}, start : ${span.start}, end : ${span.end}, \n")
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            var preEnd = -1
            if (index >= 1) {
                preEnd = editable.getSpanEnd(spans[index - 1])
            }
            val spanMessageInfo = CECIMentionedInfo();
            if (span.uid == "-1") {
                spanMessageInfo.type = AT_TYPE_ALL
                spanMessageInfo.imId = ""
            } else {
                spanMessageInfo.type = AT_TYPE_SINGLE
                spanMessageInfo.imId = span.uid
            }

            spanMessageInfo.mentionedContent = span.nickname
            if (index == 0) {
                val preMessageInfo = CECIMentionedInfo()
                preMessageInfo.type = AT_TYPE_NO
                preMessageInfo.mentionedContent = editable.subSequence(0, start).toString()
                if (!TextUtils.isEmpty(preMessageInfo.mentionedContent)) {
                    preMessageInfo.imId = ""
                    info.add(preMessageInfo)
                }
            }
            //处理前面的字符串
            if (start > 0 && preEnd != -1 && start > preEnd) {
                val preMessageInfo = CECIMentionedInfo()
                preMessageInfo.type = AT_TYPE_NO
                preMessageInfo.mentionedContent = editable.subSequence(preEnd, start).toString()
                preMessageInfo.imId = ""
                info.add(preMessageInfo)
            }
            //处理span
            info.add(spanMessageInfo)
            //处理最后一个span后面的字符串
            if (index == spans.size - 1 && end < editable.length) {
                val afterMessageInfo = CECIMentionedInfo()
                afterMessageInfo.type = AT_TYPE_NO
                afterMessageInfo.mentionedContent =
                    editable.subSequence(end, editable.length).toString()
                afterMessageInfo.imId = ""
                info.add(afterMessageInfo)
            }
        }
        LogUtil.d("infos : $info")
        info.clear()
    }

    /**
     * @操作
     * 判断当前是否是群聊，如果是群聊，判断是否存在@操作，如果有，则跳转到成员筛选列表
     */
    private fun handleAtOptions() {
        val filters = arrayOf<InputFilter>(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                val srcStr = source.toString()
                if (srcStr.equals("@", ignoreCase = true) || srcStr.equals(
                        "＠", ignoreCase = true
                    )
                ) {
                    /**
                     * 筛选浮窗
                     */
                    LogUtil.d("handleAtOptions --> etInput : $srcStr")
                    showAtBottomView()
                }
                return source!!
            }
        })

        binding.etInput.filters = filters
        handleEtInput()
    }

    /**
     * 处理输入框里面的操作
     */
    private fun handleEtInput() {
        remindHandler = RemindHandler(binding.etInput)
        remindHandler?.apply {
            addOnSpanChangedListener {
                add { member, position ->
                    LogUtil.d("新增span = $position")
                    val iterator = mTempGroupMembers.iterator()
                    while (iterator.hasNext()) {
                        if (iterator.next().id == member.id) {
                            iterator.remove()
                        }
                    }
                    mTempGroupMembers.add(member)
                }

                delete { member, position ->
                    LogUtil.d("删除span = $position")
                    if (mTempGroupMembers.isNotEmpty()) {
                        val iterator = mTempGroupMembers.iterator()
                        while (iterator.hasNext()) {
                            if (iterator.next().id == member.id) {
                                iterator.remove()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showAtBottomView() {
        atPopWindow = ImAtGroupMemberView(this)
        atPopWindow?.showImAtGroupMemberWindow(window.decorView)
        atPopWindow?.setData()
        atPopWindow?.selectMemberListener {
            val curIndex = binding.etInput.selectionStart
            if (curIndex >= 1) {
                binding.etInput.text?.replace(curIndex - 1, curIndex, "")
            }
            remindHandler?.insert("@${it.name}", it.id!!)
//            val result = binding.etInput.text?.append(it.name)
//            binding.etInput.text = result
//            result?.length?.let { length -> binding.etInput.setSelection(length) }

            atPopWindow?.hideImAtGroupMemberWindow()
        }
        KeyBoardHelper.hideKeyBoard(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CECEvent) {
        if (event.eventId == CECIMConstants.EVENT_IM_SELECT_MEMBER) {
            val map = event.param
            val data = map["data"] as MemberBean
            LogUtil.i("data : $data")
            binding.etInput.text = binding.etInput.text?.append(data.name)
            atPopWindow?.hideImAtGroupMemberWindow()

        }
    }

    override fun onNewDestroy() {
        super.onNewDestroy()
        CECEventBusHelper.unRegister(this)
    }

}