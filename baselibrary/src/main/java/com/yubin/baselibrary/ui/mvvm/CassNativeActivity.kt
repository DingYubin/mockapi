package com.yubin.baselibrary.ui.mvvm

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.yubin.baselibrary.ui.basemvvm.BaseActivity
import com.yubin.baselibrary.util.CassViewBindingUtil

abstract class CassNativeActivity<VB : ViewBinding> : BaseActivity() {

    protected lateinit var binding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CassViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        setContentView(binding.root)
    }
}