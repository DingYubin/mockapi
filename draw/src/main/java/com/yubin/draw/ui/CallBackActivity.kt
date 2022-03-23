package com.yubin.draw.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityCallbackBinding

@Route(path = RouterPath.UiPage.PATH_UI_CALLBACK)
class CallBackActivity : NativeActivity<ActivityCallbackBinding>(){

    override fun getViewBinding(): ActivityCallbackBinding =
        ActivityCallbackBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.callback_title)
    }

}