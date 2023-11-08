package com.yubin.draw.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.BaseActivity
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.EmptyUtil
import com.yubin.baselibrary.util.HandlerHelper
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.MockUtil
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.draw.R
import com.yubin.draw.bean.StoreList
import com.yubin.draw.databinding.ActivityUiBinding
import com.yubin.draw.widget.dialog.GuideDialog
import com.yubin.draw.widget.snackbar.SnackBar
import com.yubin.draw.widget.snackbar.SnackBar.Callback
import com.yubin.draw.widget.snackbar.SnackBar1
import com.yubin.draw.widget.view.GoodsItemView
import java.util.LinkedList


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
    private val handlerHelper: HandlerHelper? = null

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

        testHandler()
    }

    private fun testHandler() {
        val tips =
            "你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，你好周杰伦，"
        val message = String.format(getString(R.string.test), tips)
        val spanBuilder = SpannableStringBuilder.valueOf(message)

        spanBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(view: View) {
                    Log.d("TagX", "========   $message")
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = true
                    ds.color = ResourceUtil.getColor(R.color.st_bg_common_deep_blue)
                    ds.clearShadowLayer()
                }
            }, 0, 4,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.orderText.movementMethod = LinkMovementMethod.getInstance()
        binding.orderText.text = spanBuilder
    }

    val queue = LinkedList<String>()

    @SuppressLint("ClickableViewAccessibility")
    private fun addListener() {
        binding.snackBar.setOnClickListener {
            showNotification()
        }

        binding.snackBar1.setOnClickListener {
            if (queue.isEmpty()) {
                for (i in 0 until 6) {
                    queue.add("商品$i")
                }
                showSnackBar()
            } else {
                queue.add("商品多余")
            }

        }


        binding.snackBar2.setOnClickListener {
            queue.add("你好周杰伦")
//            SnackBar1().showSnackBar(window.decorView, "商品", "60")
        }

        binding.guide.setOnClickListener {
            val dialog = GuideDialog()
            dialog.show(this, binding.quality)
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

        binding.callbackActivity.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_CALLBACK)
                .navigation()
        }

        binding.exposureActivity.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_EXPOSURE)
                .navigation()
        }

        binding.tabActivity.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_TAB)
                .navigation()
        }

        binding.windowActivity.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_WINDOW)
                .navigation()
        }

        binding.cut.setOnClickListener {
            //截图
            val bitmap = getCurrentFrame(binding.quality)
            binding.callbackImage.setImageBitmap(bitmap)
        }

        binding.im.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_IM)
                .navigation()
        }
    }

    private fun showSnackBar() {
        if (queue.isNotEmpty()) {
            val snackBar1 = SnackBar1()
            snackBar1.showSnackBar(window.decorView, queue.removeFirst(), "60")
            snackBar1.addCallback {
                showSnackBar()
            }
        }
    }

    private fun getCurrentFrame(viewGroup: View): Bitmap? {
        // 创建对应大小的bitmap(重点)
        var bitmap = Bitmap.createBitmap(viewGroup.width, viewGroup.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        viewGroup.draw(canvas)

//        val matrix =  Matrix()
//        matrix.setScale(0.7f, 0.7f);
//        bitmap = Bitmap.createBitmap( bitmap, 0, 0,  bitmap.width, bitmap.height, matrix, false);

        return bitmap
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
                binding.storeMore.visibility = if (productSize < 8) View.GONE else View.VISIBLE

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

        setStorePic()
    }

    private fun setStorePic() {
        binding.itemContainer.removeAllViews()

        for (i in 0 until 15) {

            val view = layoutInflater.inflate(R.layout.product_inquiry_item_layout, null)
            binding.itemContainer.addView(view)
            view.setOnClickListener {
                Log.d("stores", "onClick item num: $i")
            }
        }
    }

    private fun showNotification() {
//        binding.snackBar.isEnabled = false
        val bar = SnackBar.make(
            this as BaseActivity,
            "询价单B00000001，客户补充了旧件图/零件号，请及时调整报价",
            "让汽配采购更放心",
            SnackBar.LENGTH_LONG
        ).setTimes("未来")
            .setIconLeft(R.drawable.notice_icon, 33.0f)
            .setBackground(R.drawable.notice_bg)
            .setIconPadding(1)
            .setCallback(object : Callback() {
                override fun onDismissed(snackbar: SnackBar?, event: Int) {
                    super.onDismissed(snackbar, event)
                    LogUtil.i("Notification onDismissed")
                }

                override fun onShown(snackbar: SnackBar?) {
                    super.onShown(snackbar)
                    LogUtil.i("Notification onShown")
                }
            })
        bar.showNotification()
        bar.view.setOnClickListener {
            bar.dismiss()
//            binding.snackBar.isEnabled = true
        }
    }


}