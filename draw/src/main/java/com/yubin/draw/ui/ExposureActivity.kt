package com.yubin.draw.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
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
    private val qualities = ArrayList<QualityBean>()
    private var num : Int = 0

    override fun getViewBinding(): ActivityExposureBinding =
        ActivityExposureBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.exposure)
        initView()
        initExposure()
    }

    private fun initExposure() {
        tracker = ExposureTracker("exposure_activity")
        tracker.startTask()
    }

    private fun initView() {
        binding.myRecycler.layoutManager = LinearLayoutManager(this)
        mAdapter = QualityAdapter()
        binding.myRecycler.adapter = mAdapter
        updateQualities()
        binding.srl.setOnRefreshListener {
            tracker.resetTask()
            updateQualities()
        }
    }

    private fun updateQualities() {
        for (i in 0 until 10) {
            val bean = if (i == 0){
                QualityBean(num++,VIEW_TYPE_MAIN)
            } else {
                QualityBean(num++,VIEW_TYPE_CONTENT)
            }

            qualities.add(bean)
        }
        mAdapter.submitList(qualities)
        binding.srl.isRefreshing = false
    }


    override fun onNewDestroy() {
        super.onNewDestroy()
        tracker.release()
    }

}