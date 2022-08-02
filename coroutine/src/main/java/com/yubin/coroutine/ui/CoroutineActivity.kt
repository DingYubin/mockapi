package com.yubin.coroutine.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.coroutine.databinding.ActivityCoroutineBinding

@Route(path = RouterPath.KotlinPage.PATH_KOTLIN_COROUTINE)
class CoroutineActivity : NativeActivity<ActivityCoroutineBinding>() {

    override fun getViewBinding(): ActivityCoroutineBinding =
        ActivityCoroutineBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }
    }

}