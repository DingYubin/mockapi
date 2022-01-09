package com.yubin.baselibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.yubin.baselibrary.R

/**
 *description fragment dialog 基类
 *
 */
abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    final override fun onDestroy() {
        this.onNewDestroy()
        super.onDestroy()
    }

    open fun onNewDestroy() {
        //The subclass implementation
    }

    override fun getTheme(): Int {
        return R.style.Uikit_Dialog_AlertDialog
    }
}