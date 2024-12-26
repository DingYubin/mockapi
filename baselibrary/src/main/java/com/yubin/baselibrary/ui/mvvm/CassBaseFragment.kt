package com.yubin.baselibrary.ui.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.yubin.baselibrary.util.CassViewBindingUtil
import com.yubin.baselibrary.widget.LoadingDialog

/**
 *
 * @author laiwei
 * @date 2024/9/11 11:02
 */
abstract class CassBaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    private var loading: LoadingDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CassViewBindingUtil.inflateWithGeneric(this, inflater, container, false)
        return binding.root
    }

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
        if (isDetached) {
            return
        }
        if (null == loading) {
            loading = onInitLoading()
        }
        loading?.show(parentFragmentManager, javaClass.simpleName, content, cancel)
    }

    fun dismissLoading() {
        loading?.dismissAllowingStateLoss()
    }

    fun onInitLoading(): LoadingDialog {
        return LoadingDialog.newInstance()
    }
}