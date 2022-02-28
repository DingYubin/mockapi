package com.yubin.medialibrary.video

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.databinding.ActivityVideoPlayBinding
import com.yubin.medialibrary.util.NetworkHelper

/**
 *description
 *
 *@author laiwei
 *@date create at 4/25/21 3:36 PM
 */
open class VideoPlayActivity : NativeActivity<ActivityVideoPlayBinding>() {

    var player: SimpleExoPlayer? = null

    var isPlayBeforePause = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //全屏显示
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }

        initViewStateAndListener()

        val finalUri: String? = intent.getStringExtra("url")
        val isMute = intent.getBooleanExtra("isMute", false)
        if (finalUri.isNullOrEmpty()) {
            finish()
        }
        /*
         * 检查是否有读写权限
         */
        if (selfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            selfPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            ImageLoaderManager.INSTANCE.initThreadPoolExecutor(64, this)
            if (finalUri != null) {
                initVideoStateAndListener(finalUri, isMute)
            }
        } else {
            MediaManager.instance.toast.invoke(
                getString(R.string.permission_message_dialog_default),
                this
            )
            finish()
        }
    }

    private fun initViewStateAndListener() {
        binding.btnVideoBack.onViewClick {
            finish()
        }

        binding.btnVideoBigPlay.setOnClickListener {
            player?.run {
                playWhenReady = if (playbackState == Player.STATE_ENDED) {
                    seekTo(1)
                    true
                } else {
                    !isPlaying
                }
            }
        }

    }

    private fun initVideoStateAndListener(
        url: String,
        mute: Boolean
    ) {
        if (url.startsWith("http")) {
            if (!NetworkHelper.isNetwordConnected(this)) {
                binding.videoLoadFail.visibility = View.VISIBLE
                return
            }
        }
        player = SimpleExoPlayer.Builder(this).build()
        if (mute) {
            player?.volume = 0f
        }
        binding.playerView.player = player
        player?.setMediaSource(
            DefaultMediaSourceFactory(this).createMediaSource(
                MediaItem.fromUri(
                    url
                )
            )
        )
//        showLoading()
        player?.prepare()
        player?.play()

        player?.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    Player.STATE_BUFFERING -> {
                    }//showLoading() // 缓冲中
                    Player.STATE_READY -> {
                    }//dismissLoading() // 缓冲完成
                    4 -> player?.prepare() // 播放完成
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                binding.playerView.controllerAutoShow
                binding.btnVideoBigPlay.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }

            override fun onPlayerError(error: PlaybackException) {
                binding.videoLoadFail.visibility = View.VISIBLE
//                dismissLoading()
            }
        })
    }

    override fun onNewDestroy() {
        super.onNewDestroy()
        binding.playerView.player?.release()
    }

    override fun onPause() {
        super.onPause()
        isPlayBeforePause = player?.isPlaying == true
        player?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        if (isPlayBeforePause) {
            player?.playWhenReady = true
        }
    }

    override fun getViewBinding(): ActivityVideoPlayBinding =
        ActivityVideoPlayBinding.inflate(layoutInflater)

}