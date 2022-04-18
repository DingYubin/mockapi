package com.yubin.draw.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityCallbackBinding
import com.yubin.draw.widget.viewGroup.Bubble.*

/**
 * 1、回调java 通过接口形式
 * 2、kotlin DsListenerBuilder
 * 3、livedata方式
 * 4、在lambda表达式，只支持单抽象方法模型，也就是说设计的接口里面只有一个抽象的方法，才符合lambda表达式的规则，多个回调方法不支持。
 */
@Route(path = RouterPath.UiPage.PATH_UI_CALLBACK)
class CallBackActivity : NativeActivity<ActivityCallbackBinding>(){

    override fun getViewBinding(): ActivityCallbackBinding =
        ActivityCallbackBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleWithinToolBar(R.string.callback_title)

        initView()
    }

    private fun initView() {
        val bubbleChooseButton = BubbleChooseButton(this)
        val bubbleChooseButton1 = BubbleChooseButton1(this)
        val bubbleChooseButton2 = BubbleChooseButton2(this)
        val bubbleChooseButton3 = BubbleChooseButton3(this)
        val bubbleChooseButtonDsl = BubbleChooseButtonDsl(this)

        binding.callbackForJava.setOnClickListener { v ->

            bubbleChooseButton.setOnlyListener { message ->
                LogUtil.d("callbackForJava : $message")
            }

            bubbleChooseButton.setViewAndCallback(v, object : BubbleChooseButton.OnTextViewListener{
                override fun delete() {
                    LogUtil.d("callbackForJava : 删除")
                }

                override fun top() {
                    LogUtil.d("callbackForJava : 置顶")
                }

                override fun more() {
                    LogUtil.d("callbackForJava : 更多")
                }

            })
        }

        binding.callbackForKotlin.setOnClickListener { v->

            bubbleChooseButton1.setViewAndCallback(v, object : BubbleChooseButton1.OnTextViewListener{
                override fun delete() {
                    LogUtil.d("callbackForKotlin : 删除")
                }

                override fun top() {
                    LogUtil.d("callbackForKotlin : 置顶")
                }

                override fun more() {
                    LogUtil.d("callbackForKotlin : 更多")
                }

            })
        }

        binding.callbackForKotlinCallback.setOnClickListener {
            bubbleChooseButton2.apply {
                setView(it)

                delete {
                    LogUtil.d("callbackForKotlinDs : 删除")
                }
                top {
                    LogUtil.d("callbackForKotlinDs : $it")
                }
                more {
                    LogUtil.d("callbackForKotlinDs : 更多")
                }
            }
        }

        binding.callbackForKotlinCallbackSimple.setOnClickListener{
            bubbleChooseButton3.apply {
                setViewAndCallback(it) {
                    delete { LogUtil.d("callbackForKotlinDs : 删除") }
                    top {
                        LogUtil.d("callbackForKotlinDs : $it")
                    }
                    more { LogUtil.d("callbackForKotlinDs : 更多") }
                }
            }
        }

        binding.callbackForKotlinDs.setOnClickListener {
            bubbleChooseButtonDsl.apply {
                init(it)
                addListener {
                    //可以任意选择需要回调的函数，不必要完全重写
                    delete {
                        LogUtil.d("callbackForKotlinDs : $it")
                    }
                    top {
                        LogUtil.d("callbackForKotlinDs : $it")
                    }
                    more {
                        LogUtil.d("callbackForKotlinDs : $it")
                    }
                }
            }
        }

        binding.callbackForKotlinLivedata.setOnClickListener {

        }
    }


}