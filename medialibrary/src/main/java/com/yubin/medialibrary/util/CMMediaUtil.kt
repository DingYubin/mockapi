package com.yubin.medialibrary.util

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yubin.baselibrary.widget.ToastUtil
import com.yubin.medialibrary.R
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.manager.*
import com.yubin.medialibrary.permissionHelper.CECPermissionHelper
import com.yubin.medialibrary.permissionHelper.CECPermissionListener

/**
 *description 封装启动媒体组件，包含权限判断
 *
 *@author yubin
 *@date create at 4/27/21 1:49 PM
 */
object CMMediaUtil {

    fun startCamera(
        strategy: CameraStrategy,
        context: Context,
        callBack: IMediaCallBack? = null
    ) {
        CECPermissionHelper.requestForCamera(object : CECPermissionListener {
            override fun onPermissionGranted() {
                CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                    override fun onPermissionGranted() {
                        //打开相机
                        MediaManager.instance.start(strategy, context, callBack)
                    }

                    override fun onPermissionDeclined(permission: String?) {
                        ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),
                            Toast.LENGTH_SHORT)
                    }

                    override fun onPermissionDenied(permission: String?) {
                        ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
                    }
                })
            }

            override fun onPermissionDeclined(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }

            override fun onPermissionDenied(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }
        })
    }

    fun startAlbum(
        strategy: AlbumStrategy,
        context: Context,
        callBack: IMediaCallBack? = null
    ) {
        CECPermissionHelper.requestForStorage(object : CECPermissionListener {
            override fun onPermissionGranted() {
                //打开相册
                MediaManager.instance.start(strategy, context, callBack)
            }

            override fun onPermissionDeclined(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }

            override fun onPermissionDenied(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }
        })

    }

    fun startClipPicture(
        strategy: ClipPictureStrategy,
        context: AppCompatActivity,
        imagePath: String,
        requestCode: Int
    ) {

        CECPermissionHelper.requestForStorage(object : CECPermissionListener {
            override fun onPermissionGranted() {
                MediaManager.instance.startClipPicture(context, imagePath, strategy, requestCode)
            }

            override fun onPermissionDeclined(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }

            override fun onPermissionDenied(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }
        })

    }

    fun startVideoPlay(
        strategy: VideoStrategy,
        context: Context,
        callBack: IMediaCallBack? = null
    ) {
        CECPermissionHelper.requestForStorage(object : CECPermissionListener {
            override fun onPermissionGranted() {
                //打开视频播放
                MediaManager.instance.start(strategy, context, callBack)
            }

            override fun onPermissionDeclined(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }

            override fun onPermissionDenied(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }
        })
    }

    fun startVideoRecord(
        strategy: RecordStrategy,
        context: Context,
        callBack: IMediaCallBack? = null
    ) {

        CECPermissionHelper.requestForVideoRecord(object : CECPermissionListener {
            override fun onPermissionGranted() {
                CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                    override fun onPermissionGranted() {
                        //打开视频录制
                        MediaManager.instance.start(strategy, context, callBack)
                    }

                    override fun onPermissionDeclined(permission: String?) {
                        ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
                    }

                    override fun onPermissionDenied(permission: String?) {
                        ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
                    }
                })
            }

            override fun onPermissionDeclined(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }

            override fun onPermissionDenied(permission: String?) {
                ToastUtil.showToast(context.getString(R.string.permission_message_dialog_default),Toast.LENGTH_SHORT)
            }
        })
    }

}