package com.yubin.draw.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
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
import com.yubin.draw.widget.viewGroup.exposure.manager.ExposureManager
import com.yubin.draw.widget.viewGroup.exposure.tracker.ExposureTracker
import com.yubin.draw.widget.viewGroup.exposure.utils.ExposureHelper
import com.yubin.draw.widget.viewGroup.exposure.utils.HomePageExposeUtil

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
    private var num: Int = 0

    override fun getViewBinding(): ActivityExposureBinding =
        ActivityExposureBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.exposure)
        initView()
        initExposure()
//        addExposureListener()
    }

    private fun addExposureListener() {
        val exposure = HomePageExposeUtil()
        exposure.setRecyclerItemExposeListener(
            binding.myRecycler
        ) { visible, position -> LogUtil.d("屏幕内 第$position 位置，可见状态 ：$visible") }
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
        binding.bottom.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                getMeasure()
                binding.bottom.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getMeasure() {
        val view = binding.bottom
        LogUtil.i("width = ${view.width}, height = ${view.height}")
        LogUtil.i("MeasuredWidth = ${view.measuredWidth}, MeasuredHeight = ${view.measuredHeight}")
        //相对window 位置
        val location = IntArray(2)
        view.getLocationInWindow(location)
        LogUtil.i("x = ${location[0]}, y = ${location[1]}")

        //相对屏幕的绝对位置
        val location2 = IntArray(2)
        view.getLocationOnScreen(location2)
        LogUtil.i("x = ${location2[0]}, y = ${location2[1]}")

        //相对父控件位置而言
        LogUtil.i("left = ${view.left}, top = ${view.top}, right = ${view.right}, bottom = ${view.bottom}")

        //view可见部分 相对于 屏幕的坐标
        val globalRect = Rect()
        view.getGlobalVisibleRect(globalRect)
        LogUtil.i("left = ${globalRect.left}, top = ${globalRect.top}, right = ${globalRect.right}, bottom = ${globalRect.bottom}")
        LogUtil.i("rect.width = ${globalRect.width()}, rect.height = ${globalRect.height()}")


        ExposureHelper.exposureTopHigh = location2[1]
//        ExposureHelper.exposureLeftWidth = location2[0]
    }

    private fun initView() {
        binding.myRecycler.layoutManager = LinearLayoutManager(this)
        mAdapter = QualityAdapter()
        binding.myRecycler.adapter = mAdapter
        updateQualities(false)
        binding.srl.setOnRefreshListener {
            ExposureManager.instance.reset("exposure_activity")
            updateQualities(true)
        }
    }

    private fun updateQualities(isRefresh: Boolean) {
        val qualities: MutableList<QualityBean> = ArrayList()
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
    }


    override fun onNewDestroy() {
        ExposureManager.instance.remove("exposure_activity")
        tracker.release()
        ExposureManager.instance.clear()
        super.onNewDestroy()

    }

}