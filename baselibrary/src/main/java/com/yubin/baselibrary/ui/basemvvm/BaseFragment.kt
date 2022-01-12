package com.yubin.baselibrary.ui.basemvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.yubin.baselibrary.widget.LoadingDialog

/**
 * Description
 * @Author YWQ
 * @Date 2020/7/30 5:30 PM
 */
abstract class BaseFragment <VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    private var loading: LoadingDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    final override fun onDestroy() {
        this.onNewDestroy()
        super.onDestroy()
//        _binding = null
    }

    open fun onNewDestroy() {
        //The subclass implementation
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * 无文案可取消的loading
     */
    fun showLoading() {
        this.showLoading(null, true)
    }

    /**
     * 无文案可设置是否取消的loading
     */
    fun showLoading(cancel: Boolean) {
        this.showLoading(null, cancel)
    }

    /**
     * 带文案可取消的loading
     */
    fun showLoading(content: String?) {
        this.showLoading(content, true)
    }

    /**
     * 带文案可设置是否取消的loading
     */
    fun showLoading(content: String?, cancel: Boolean) {
        if (null == loading) {
            loading = LoadingDialog.newInstance()
        }
        loading?.show(parentFragmentManager, javaClass.simpleName, content, cancel)
    }

    fun dismissLoading() {
        loading?.dismissAllowingStateLoss()
    }
}