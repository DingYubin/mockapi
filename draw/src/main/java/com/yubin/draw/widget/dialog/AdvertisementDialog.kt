package com.yubin.draw.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.draw.R

class AdvertisementDialog : DialogFragment() {

    private var topClose: AppCompatImageView? = null
    private var bottomClose: AppCompatImageView? = null
    private var ivAdvertisement: AppCompatImageView? = null

    private var closeListener: (() -> Unit)? = null
    private var imageClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = dialog?.window
        val params = window?.attributes
        window?.attributes = params
        window?.setBackgroundDrawable(ColorDrawable())

        val view = inflater.inflate(R.layout.dialog_advertisement, container, false)
        topClose = view.findViewById(R.id.iv_top_close)
        bottomClose = view.findViewById(R.id.iv_bottom_close)
        ivAdvertisement = view.findViewById(R.id.iv_advertisement)

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Uikit_Dialog_TransparentNoTitle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgUrl = arguments?.getString("imgUrl")
        val position = arguments?.getString("closePosition")
        val isTouchOutside = arguments?.getBoolean("isTouchOutside")

        loadImage(imgUrl, view)
        setCloseIconPosition(position)
        setCanceledOnTouchOutside(isTouchOutside ?: true)
        topClose?.onViewClick {
            closeListener?.invoke()
            dismiss()
        }

        bottomClose?.onViewClick {
            closeListener?.invoke()
            dismiss()
        }

        ivAdvertisement?.onViewClick {
            imageClickListener?.invoke()
        }
    }

    private fun setCanceledOnTouchOutside(isTouch: Boolean) {
        dialog?.setCanceledOnTouchOutside(isTouch)
    }

    private fun setCloseIconPosition(position: String?) {
        when (position) {
            TOP -> {
                topClose?.visibility = View.VISIBLE
                bottomClose?.visibility = View.GONE
            }

            BOTTOM -> {
                bottomClose?.visibility = View.VISIBLE
                topClose?.visibility = View.GONE
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        closeListener?.invoke()
        super.onCancel(dialog)
    }

    private fun loadImage(imgUrl: String?, view: View) {

        if (!TextUtils.isEmpty(imgUrl)) {
            // 从网络上拉取网络图片
            ivAdvertisement?.let {
                Glide.with(view.context)
                    .load(imgUrl)
                    .into(it)
            }
        }
    }

    fun closeListener(listener: () -> Unit) {
        this.closeListener = listener
    }

    fun imageClickListener(listener: () -> Unit) {
        this.imageClickListener = listener
    }

    companion object {
        val TOP: String = "top"
        val BOTTOM: String = "bottom"

        fun newInstance(
            imgUrl: String,
            closePosition: String,
            isTouchOutside: Boolean
        ): AdvertisementDialog {
            val instance = AdvertisementDialog()
            val bundle = Bundle()
            bundle.putString("imgUrl", imgUrl)
            bundle.putString("closePosition", closePosition)
            bundle.putBoolean("isTouchOutside", isTouchOutside)
            instance.arguments = bundle
            return instance
        }
    }

}