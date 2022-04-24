package com.yubin.draw.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.adapter.QualityAdapter
import com.yubin.draw.adapter.QualityAdapter.Companion.VIEW_TYPE_CONTENT
import com.yubin.draw.adapter.QualityAdapter.Companion.VIEW_TYPE_MAIN
import com.yubin.draw.bean.QualityBean
import com.yubin.draw.databinding.ActivityExposureBinding
import com.yubin.draw.widget.viewGroup.exposure.ExposureTracker

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/04/14
 * desc    : 曝光页面
 * version : 1.0
</pre> *
 */
@Route(path = RouterPath.UiPage.PATH_UI_EXPOSURE)
class ExposureActivity : NativeActivity<ActivityExposureBinding>() {
    private lateinit var mAdapter: QualityAdapter
    private lateinit var tracker: ExposureTracker
    private var num : Int = 0

    override fun getViewBinding(): ActivityExposureBinding =
        ActivityExposureBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.exposure)
        initView()
        initExposure()
    }

    override fun onStart() {
        tracker.startTask()
        LogUtil.d("onStart() ")
        super.onStart()
    }

    override fun onStop() {
        tracker.clearTask()
        LogUtil.d("onStop() ")
        super.onStop()
    }

    private fun initExposure() {
        tracker = ExposureTracker("exposure_activity")
    }

    private fun initView() {
        binding.myRecycler.layoutManager = LinearLayoutManager(this)
        mAdapter = QualityAdapter()
        binding.myRecycler.adapter = mAdapter
        updateQualities(false)
        binding.srl.setOnRefreshListener {
            tracker.reset()
//            tracker.refresh()
            updateQualities(true)
        }
    }

    private fun updateQualities(isRefresh : Boolean) {
        val qualities : MutableList<QualityBean> = ArrayList()
        if (!isRefresh) {
            updateData(qualities)
            mAdapter.submitList(qualities)
        } else {
            updateData(qualities)
            mAdapter.addList(qualities)
            binding.srl.isRefreshing = false
        }
    }

    private fun updateData(qualities: MutableList<QualityBean>) {
        for (i in 0 until 20) {
            val bean = if (i == 0) {
                QualityBean(num, "event_${num}", VIEW_TYPE_MAIN)
            } else {
                QualityBean(num, "event_${num}", VIEW_TYPE_CONTENT)
            }
            num++
            qualities.add(bean)
        }
        LogUtil.d("ExposureHandler position = $qualities,")
    }


    override fun onNewDestroy() {
        super.onNewDestroy()
        tracker.release()
    }

}