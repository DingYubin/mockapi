package com.yubin.medialibrary.picture

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CMStatusBarUtil
import com.yubin.medialibrary.R
import com.yubin.medialibrary.databinding.ActivityClipPictureBinding
import com.yubin.medialibrary.util.CMStreamUtil
import com.yubin.medialibrary.util.DeviceHelper
import com.yubin.medialibrary.util.SaveImageUtils
import com.yubin.medialibrary.util.UnitHelper

/**
 * @introduce : 裁剪图片界面
 *
 * creattime : 2021/11/25
 *
 * author : xiongyp
 *
 **/
class ClipPictureActivity : NativeActivity<ActivityClipPictureBinding>() {
    private var bitmap: Bitmap? = null
    private var cropImageView: QRCropImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        supportActionBar?.hide()
        CMStatusBarUtil.setStatusColor(this, false, true, ContextCompat.getColor(this, R.color.color_ccffffff))
        val imgPath = intent.getStringExtra("imagePath") ?: ""
        if (imgPath.isBlank()) {
            finish()
            return
        }
        val screenWidth = DeviceHelper.screenWidthWithContext(this)
        val screenHeight = DeviceHelper.screenHeightWithContext(this)
        bitmap = CMStreamUtil.getSmallBitmap(imgPath, screenWidth, screenHeight)
        //设置图片显示宽高为屏幕宽度
        val params: RelativeLayout.LayoutParams =
                RelativeLayout.LayoutParams(screenWidth, screenHeight)
        cropImageView =
            QRCropImageView(this, bitmap)
        cropImageView!!.id = R.id.cropImageViewId
        var marginTop = screenHeight * intent.getFloatExtra("screenHeightFloat", 0.3f)
        var rectHeight = screenWidth * intent.getFloatExtra("screenWidthFloat", 0.6f)
        cropImageView?.marginTop = marginTop.toInt()
        cropImageView?.rectHeight = rectHeight.toInt()
        binding.imgLayout.addView(cropImageView, params)
        binding.tvClipmsg.text = intent.getStringExtra("clipmsg")

        val layoutParams: ConstraintLayout.LayoutParams = binding.tvClipmsg.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(0, (marginTop + rectHeight + UnitHelper.dp2px(this, 10f)).toInt(), 0, 0)
        binding.tvClipmsg.layoutParams = layoutParams
        binding.tvCancel.onViewClick {
            finish()
        }

        binding.tvSure.onViewClick {
            doOk()
        }

    }

    private fun doOk() {
        var bitmap = cropImageView?.createCropBitmap()
        bitmap?.let {
            recognize(bitmap, "")
        }
    }

    private fun recognize(bitmap: Bitmap, result: String) {
        val imagePath = SaveImageUtils.saveImageUri(
                bitmap,
                applicationContext, result
        )
        val intent = Intent()
        intent.putExtra("imagePath", imagePath)
        setResult(RESULT_OK, intent)
        finish()
    }


    override fun onNewDestroy() {
        super.onNewDestroy()
        if (bitmap != null && bitmap?.isRecycled == false) {
            bitmap?.recycle()
        }
    }

    override fun getViewBinding(): ActivityClipPictureBinding = ActivityClipPictureBinding.inflate(layoutInflater)

}