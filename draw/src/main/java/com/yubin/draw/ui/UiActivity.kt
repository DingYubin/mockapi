package com.yubin.draw.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.BaseActivity
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.EmptyUtil
import com.yubin.baselibrary.util.MockUtil
import com.yubin.draw.R
import com.yubin.draw.bean.StoreList
import com.yubin.draw.databinding.ActivityUiBinding
import com.yubin.draw.widget.snackbar.SnackBar
import com.yubin.draw.widget.view.GoodsItemView


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

    private var goodsItemViews: List<GoodsItemView>? = null

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
        bindData()
        addListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListener() {
        binding.snackBar.setOnClickListener {
            showNotification()
        }

        binding.horizontalScrollView.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_UP -> {
                    val firstView = (v as HorizontalScrollView).getChildAt(0)
                    if (firstView.measuredWidth <= v.getScrollX() + v.getWidth()) {
                        //加载数据代码
                        Log.d("TagX", "========   到最右边")
                    }
                }
            }
            false
        }
    }

    private fun initView() {

        goodsItemViews = arrayListOf(
            GoodsItemView(findViewById(R.id.goods_item1)),
            GoodsItemView(findViewById(R.id.goods_item2)),
            GoodsItemView(findViewById(R.id.goods_item3)),
            GoodsItemView(findViewById(R.id.goods_item4)),
            GoodsItemView(findViewById(R.id.goods_item5)),
            GoodsItemView(findViewById(R.id.goods_item6)),
            GoodsItemView(findViewById(R.id.goods_item7)),
            GoodsItemView(findViewById(R.id.goods_item8))
        )

    }

    private fun bindData() {
        //mock 数据
        val mockJson: String = MockUtil.stringFromAssets(context, "goods.json")
        val data: StoreList = Gson().fromJson(mockJson, StoreList::class.java)
        val stores = data.stores
        for (store in stores) {
            if (EmptyUtil.isCollectionNotEmpty(store.products)) {
                val viewSize = goodsItemViews?.size
                val productSize = store.products.size
                binding.more.visibility = if (productSize < 8) View.GONE else View.VISIBLE

                for (i in 0 until viewSize!!) {
                    val goodsItemHolder = goodsItemViews?.get(i)
                    if (i < productSize) {
                        goodsItemHolder?.setData(store.products[i], 2)
                    } else {
                        goodsItemHolder?.setData(null, 1)
                    }
                }
            }
        }
    }

    private fun showNotification() {
        binding.snackBar.isEnabled = false
        val bar = SnackBar.make(
            this as BaseActivity,
            "询价单B00000001，客户补充了旧件图/零件号，请及时调整报价",
            "让汽配采购更放心",
            SnackBar.LENGTH_LONG
        )   .setTimes("未来")
            .setIconLeft(R.drawable.notice_icon, 33.0f)
            .setBackground(R.drawable.notice_bg)
            .setIconPadding(1)
        bar.showNotification()
        bar.view.setOnClickListener {
            bar.dismiss()
            binding.snackBar.isEnabled = true
        }
    }
}