package com.yubin.draw.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.BaseActivity
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityUiBinding
import com.yubin.draw.widget.snackbar.SnackBar

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/03/03
 * desc    : 绘制页面显示
 * version : 1.0
</pre> *
 */
@Route(path = RouterPath.UiPage.PATH_UI_DRAW)
class UiActivity : NativeActivity<ActivityUiBinding>() {

    companion object {
        @JvmStatic
        fun openUiActivity(activity: Activity) {
            val intent = Intent(activity, UiActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun getViewBinding(): ActivityUiBinding =
        ActivityUiBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }

        initView()
    }

    private fun initView() {
        binding.snackBar.setOnClickListener {
            val bar = SnackBar.make(this as BaseActivity, "询价单B00000001，客户补充了旧件图/零件号，请及时调整报价", "让汽配采购更放心", SnackBar.LENGTH_LONG)
                .setIconLeft(R.drawable.notice_icon, 33.0f)
                .setBackground(R.drawable.notice_bg)
                .setIconPadding(1)
            bar.showNotification()
            bar.view.setOnClickListener {
                bar.dismiss()
            }
        }
    }
}