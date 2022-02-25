package com.yubin.baselibrary.ui.basemvvm

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.viewbinding.ViewBinding


/**
 * Description
 * @Date 2020/7/30 4:52 PM
 */
abstract class NativeActivity<VB : ViewBinding> : BaseActivity() {

    companion object {
        @JvmStatic
        var lastClickTime: Long = 0
    }

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

    /**
     * 检测是否有相应权限
     */
    fun selfPermissionGranted(context: Context, permission: String): Boolean {
        val targetSdkVersion = context.applicationInfo.targetSdkVersion
        return if (targetSdkVersion >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            //targetSdkVersion<23,需要用另一种方式获取权限
            PermissionChecker.checkSelfPermission(
                context,
                permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

}