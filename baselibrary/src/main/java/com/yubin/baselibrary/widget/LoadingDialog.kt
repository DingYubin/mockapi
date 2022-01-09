package com.yubin.baselibrary.widget

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.yubin.baselibrary.R
import com.yubin.baselibrary.databinding.DialogLoadingBinding
import com.yubin.baselibrary.ui.BaseDialog
import com.yubin.baselibrary.util.CMDisplayHelper.dp

/**
 *description 加载loading
 *
 */
class LoadingDialog : BaseDialog<DialogLoadingBinding>() {

    companion object {
        fun newInstance(): LoadingDialog {
            return LoadingDialog()
        }
    }

    private lateinit var animation: AnimationDrawable
    private var content: String? = null
    private var cancel: Boolean = true

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = DialogLoadingBinding.inflate(inflater, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Uikit_Dialog_Transparent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        if (window != null) {
            val attributes = window.attributes
            attributes.width = 120.dp
            attributes.height = 120.dp
            window.attributes = attributes
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.setCanceledOnTouchOutside(false)
        animation = binding.loading.drawable as AnimationDrawable
        dialog?.setOnShowListener {
            if (content.isNullOrEmpty()) {
                binding.bg.setBackgroundResource(R.drawable.uikit_loading_bg_10)
                binding.loadingText.visibility = View.GONE
            } else {
                binding.bg.setBackgroundResource(R.drawable.uikit_loading_bg)
                binding.loadingText.visibility = View.VISIBLE
                binding.loadingText.text = content
            }
            isCancelable = cancel

            animation.start()
        }
        dialog?.setOnDismissListener {
            dismissAllowingStateLoss()
            animation.stop()
            binding.loading.clearAnimation()
        }
    }

    fun show(
        manager: FragmentManager,
        tag: String?,
        content: String? = null,
        cancel: Boolean = true,
    ) {
        if (isAdded) {
            manager.beginTransaction().remove(this).commit()
        }
        this.content = content
        this.cancel = cancel
        super.show(manager, tag)
    }
}