package com.yubin.medialibrary.video

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.medialibrary.R
import com.yubin.medialibrary.camera.MediaManager

/**
 * description:
 * @author 李良军
 * @date 2021/2/24
 */
class RecordExitDialog : DialogFragment() {
    private var mExit: AppCompatTextView? = null
    private var mRecapture: AppCompatTextView? = null
    private var mCancel: AppCompatTextView? = null
    private var mListener: ClickListener? = null

    companion object {
        fun newInstance(): RecordExitDialog {
            return RecordExitDialog()
        }
    }

    fun setListener(listener: ClickListener?) {
        mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_record_exit_prompt, null)
    }

    override fun getTheme(): Int {
        return R.style.Uikit_Dialog_AlertDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mExit = view.findViewById(R.id.tv_exit)
        mRecapture = view.findViewById(R.id.tv_recapture)
        mCancel = view.findViewById(R.id.tv_cancel)
        MediaManager.instance.mediaStyle?.let {
            mCancel?.setTextColor(ContextCompat.getColor(requireContext(), it.cancelBtnColor))
        }
        mExit?.onViewClick {
            mListener?.exit()
            dismiss()
        }
        mRecapture?.onViewClick {
            if (null != mListener) {
                mListener!!.recapture()
            }
            dismiss()
        }
        mCancel?.onViewClick(View.OnClickListener {
            dismiss()
        })
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val params = window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = params
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface ClickListener {
        fun exit()
        fun recapture()
    }

}