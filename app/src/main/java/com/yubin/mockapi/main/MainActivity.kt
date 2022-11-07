package com.yubin.mockapi.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.keytop.ndkbuilddemo.MyNdk
import com.yubin.account.user.ui.AccountActivity
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CECDeviceHelper
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.baselibrary.util.LogUtil
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.manager.CameraFinder
import com.yubin.medialibrary.manager.CameraStrategy
import com.yubin.medialibrary.manager.IMediaCallBack
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.CMMediaUtil
import com.yubin.mockapi.R
import com.yubin.mockapi.databinding.ActivityMainBinding
import com.yubin.mockapi.tinker.TinkerManager
import com.yubin.mvp.ui.MvpLoginActivity
import java.io.File

class MainActivity : NativeActivity<ActivityMainBinding>() {

    /**
     * 相机宽度
     */
    var w = 0

    /**
     * 相机高度
     */
    var h = 0

    //补丁文件后缀名
    private val FILE_END = ".apk"

    //apatch文件路径
    private lateinit var mPatchDir: String

    private var mSoLibraryContentTv: TextView? = null

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }
        askForWriteRequiredPermissions()
        askForRequiredPermissions()
        w = CECDeviceHelper.screenWidthWithContext(this) - 28.dp
        h = 200.dp

        val config: MediaManager.Config = MediaManager.Config()
        MediaManager.Companion.init(config)

        findViewById<View>(R.id.goto_user_page).setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        mSoLibraryContentTv = findViewById(R.id.content_from_so)
        findViewById<View>(R.id.mvp).setOnClickListener {
            MvpLoginActivity.openLoginActivity(
                this
            )
        }

        findViewById<View>(R.id.mvvm).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.MvvmPage.PATH_MVVM_LOGIN)
                .navigation()
        }

        findViewById<View>(R.id.mvx).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.AccountPage.PATH_MVX_LOGIN)
                .navigation()
        }

        findViewById<View>(R.id.im).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.ImPage.PATH_IM_CONVERSATION)
                .navigation()
        }

        findViewById<View>(R.id.draw).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_DRAW)
                .navigation()
        }

        findViewById<View>(R.id.rxjava).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.RxPage.PATH_RX_JAVA)
                .navigation()
        }

        findViewById<View>(R.id.kotlin_coroutine).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.KotlinPage.PATH_KOTLIN_COROUTINE)
                .navigation()
        }

        findViewById<View>(R.id.camera).setOnClickListener {
//            ARouter.getInstance()
//                .build(RouterPath.MediaPage.PATH_MEDIA_CAMERA)
//                .navigation()

            CMMediaUtil.startCamera(
                CameraStrategy(
                    isShowVideo0 = false,
                    maxCount0 = 1,
                    cameraFinder = CameraFinder(true, w, h)
            ).apply {
                selectedBtnText = "确定"
            }, this,
                object : IMediaCallBack {
                    override fun result(medias: ArrayList<MediaInfo>) {
                        Log.d("camera", "url: ${medias[0].uri}")
                        showView(medias[0].uri)
                    }
                })
        }

        //加载补丁
        binding.loadTinker.setOnClickListener {
            createPatchPath()
            val path = File(getPatchPath())
            if (path.exists()){
                Log.d("xsh", " path.length--> " + path.length())
            }
            TinkerManager.loadPatchPatch(getPatchPath())
        }

        binding.clickGetSoLibraryData.setOnClickListener {
            getLibraryMsg()
        }
    }

    private fun getLibraryMsg() {
        val myNdk = MyNdk()
        myNdk.hotUpdate(this)
        if (!TextUtils.isEmpty(myNdk.myString)) {
            mSoLibraryContentTv?.text = myNdk.myString
        }
    }

    private fun createPatchPath() {
//        mPatchDir = externalCacheDir?.absolutePath + "/xsh/"
        mPatchDir = Environment.getExternalStorageDirectory().absolutePath + "/xsh/"
        LogUtil.i("Tinker mPatchDir update ======================= : $mPatchDir")

        //创建文件夹
        val file = File(mPatchDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun getPatchPath(): String {
        return "${mPatchDir}app-debug-patch_signed_7zip.apk"
    }

    private fun showView(uri: String) {
        val imageView = findViewById<ImageView>(R.id.my_img)

        // 从网络上拉取网络图片
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun askForRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 16) {
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun askForWriteRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        if (!hasRequiredWritePermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun hasRequiredWritePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 16) {
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        }
    }

}