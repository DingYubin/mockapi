package com.yubin.baselibrary.ui.basemvvm

import android.os.Bundle
import androidx.viewbinding.ViewBinding


/**
 * Description
 * @Date 2020/7/30 4:52 PM
 */
abstract class NativeActivity<VB : ViewBinding> : BaseActivity() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        super.setContentView(binding.root)
    }

    final override fun onDestroy() {
        this.onNewDestroy()
        super.onDestroy()
    }

    open fun onNewDestroy() {
        //The subclass implementation
    }

    abstract fun getViewBinding(): VB

}