package com.yubin.im.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.im.databinding.ActivityImBinding
import com.yubin.im.manager.ChatAndStaffLayoutManager

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/02/12
 * desc    : 会话
 * version : 1.0
</pre> *
 */
@Route(path = RouterPath.ImPage.PATH_IM_CONVERSATION)
class ConversationActivity : NativeActivity<ActivityImBinding>() {

    private var chatAndStaffLayoutManager: ChatAndStaffLayoutManager? = null

    override fun getViewBinding(): ActivityImBinding {
        return ActivityImBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }

        chatAndStaffLayoutManager = ChatAndStaffLayoutManager(this)
        binding.prtRecyclerView.layoutManager = chatAndStaffLayoutManager
        binding.header.attachTo(binding.prtRecyclerView, true)
    }

}