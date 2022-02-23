package com.yubin.medialibrary.camera

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yubin.medialibrary.manager.IMediaCallBack
import com.yubin.medialibrary.manager.MediaStrategy
import com.yubin.medialibrary.manager.MediaStyle
import com.yubin.medialibrary.util.ClipPictureStrategy

/**
 *description
 *
 *@author laiwei
 *@date create at 4/25/21 5:05 PM
 */
class MediaManager private constructor(mConfig: Config) {
    var toast: (String, Context?) -> Unit = { content, context ->
        context?.let {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
        }
    }

    var saveValue: (String, String) -> Unit = { _: String, _: String -> }
    var getValue: (String) -> String = { "" }

    var showLoading: (AppCompatActivity, String, Boolean) -> () -> Unit =
        { _: AppCompatActivity, _: String, _: Boolean -> {} }

    var mediaStyle: MediaStyle? = null

    init {
        toast = mConfig.toast
        saveValue = mConfig.saveValue
        getValue = mConfig.getValue
        showLoading = mConfig.showLoading
        mediaStyle = mConfig.mediaStyle
    }

    var callBack: IMediaCallBack? = null

    fun start(strategy: MediaStrategy, context: Context, callBack: IMediaCallBack? = null) {
        this.callBack = callBack
        when (strategy.strategy) {
            //拍照
            MediaStrategy.TYPE_TAKE_CAMERA -> {
                val intent = Intent()
                intent.setClass(context, CameraActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("strategy", strategy)
                context.startActivity(intent)
            }
            //打开相册
            MediaStrategy.TYPE_OPEN_ALBUM -> {
//                val intent = Intent()
//                intent.setClass(context, ImagePickerActivity::class.java)
//                intent.putExtra("strategy", strategy)
//                context.startActivity(intent)
            }
            //播放视频
            MediaStrategy.TYPE_PLAY_VIDEO -> {
//                val intent = Intent()
//                intent.setClass(context, VideoPlayActivity::class.java)
//                strategy as VideoStrategy
//                intent.putExtra("url", strategy.url)
//                intent.putExtra("isMute", strategy.isMute)
//                context.startActivity(intent)
            }
            //录制视频
            MediaStrategy.TYPE_RECORD_VIDEO -> {
//                val intent = Intent()
//                intent.setClass(context, VideoRecordActivity::class.java)
//                strategy as RecordStrategy
//                intent.putExtra("seconds", strategy.seconds)
//                context.startActivity(intent)
            }
        }
    }
    fun startClipPicture(context: AppCompatActivity, imagePath: String, strategy: ClipPictureStrategy, requestCode:Int) {
        //进入二维码扫描界面
//        val intent = Intent()
//        intent.setClass(context, ClipPictureActivity::class.java)
//        intent.putExtra("imagePath", imagePath)
//        intent.putExtra("clipmsg", strategy.clipmsg)
//        intent.putExtra("screenHeightFloat", strategy.screenHeightFloat)
//        intent.putExtra("screenWidthFloat", strategy.screenWidthFloat)
//        context.startActivityForResult(intent,requestCode)
    }

    private object SingletonHolder {
        val holder: MediaManager by lazy {
            MediaManager(mConfig)
        }
    }

    companion object {
        private lateinit var mConfig: Config
        fun init(config: Config) {
            mConfig = config
        }

        val instance by lazy { SingletonHolder.holder }
    }

    class Config {
        var toast: (String, Context?) -> Unit = { content, context ->
            context?.let {
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
            }
        }

        var saveValue: (String, String) -> Unit = { _: String, _: String -> }
        var getValue: (String) -> String = { "" }

        var showLoading: (AppCompatActivity, String, Boolean) -> () -> Unit =
            { _: AppCompatActivity, _: String, _: Boolean -> {} }

        var mediaStyle: MediaStyle? = null
    }
}