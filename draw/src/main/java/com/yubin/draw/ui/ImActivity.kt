package com.yubin.draw.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityUiImBinding
import com.yubin.draw.widget.viewGroup.im.ImAtGroupMemberView

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/04/14
 * desc    : im 试图
 * version : 1.0
</pre> *
 */
@Route(path = RouterPath.UiPage.PATH_UI_IM)
class ImActivity : NativeActivity<ActivityUiImBinding>() {

    override fun getViewBinding(): ActivityUiImBinding =
        ActivityUiImBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitleWithinToolBar(R.string.im_style)

        initView()
    }

    private fun initView() {
        handleAtOptions()

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
    }

    private fun showAtBottomView() {
        ImAtGroupMemberView(this).showImAtGroupMemberWindow(window.decorView)
    }
}