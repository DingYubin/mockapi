package com.yubin.draw.widget.ViewGroup.Bubble

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.yubin.draw.R

/**
 * Created by yubin.ding on 2017/5/26.
 * 在Kotlin的一个类中实现了DSL配置回调非常简单主要就三步
 * 1、定义一个回调的Builder类，并且在类中定义回调lamba表达式对象成员，最后再定义Builder类的成员函数，这些函数就是暴露给外部回调的函数。
 * 2、然后，在类中声明一个ListenerBuilder的实例引用，并且暴露一个设置该实例对象的一个方法，也就是我们常说的注册事件监听或回调的方法，
 *    类似setOnClickListenter这种。但是需要注意的是函数的参数是带ListenerBuilder返回值的lamba
 * 3、最后在触发相应事件调用Builder实例中lamba即可
 */
class BubbleChooseButtonDsl(context: Context?, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val centerWindow: BubblePopupWindow = BubblePopupWindow(context)
    private lateinit var deleteTv : TextView
    private lateinit var topTv : TextView
    private lateinit var moreTv : TextView

    fun init(view: View) {
        initView(view)
        addViewListener()
    }

    private fun initView(view: View) {
        val bubbleView = inflate(context, R.layout.top_layout_popup_view, null)
        deleteTv = bubbleView.findViewById(R.id.delete)
        topTv = bubbleView.findViewById(R.id.top)
        moreTv = bubbleView.findViewById(R.id.more)
        centerWindow.setBubbleView(bubbleView)
        centerWindow.show(view, Gravity.TOP, 30f)
    }

    private fun closePopupWindow() {
        if (centerWindow.isShowing) centerWindow.dismiss()
    }

    /**
     * internal var deleteAction: ((str : String) -> Unit)? = null
     * ==等价
     * internal var deleteAction: ((String) -> Unit)? = null
     *
     * inner关键字：
     *           kotlin中支持类的嵌套（内部类），不过和java中不一样（java中包含一个指向外部类的对象的引用），
     *           kotlin中所有的内部类默认为静态的，这样很好的减少了内存泄漏问题。如果需要在内部类引用外部类的对象，
     *           可以使用inner声明内部类，使内部类变为非静态的，通过this@外部类名，指向外部类。
     * internal关键字：
     *           模块--可见性修饰符 internal 意味着该成员只在相同模块内可见。
     *
     * 1、定义一个回调的Builder类，并且在类中定义回调lamba表达式对象成员，最后再定义Builder类的成员函数，这些函数就是暴露给外部回调的函数。个人习惯把它作为一个类的内部类。类似下面这样
     */
    inner class ListenerBuilder {
        internal var mDeleteAction: ((String) -> Unit)? = null
        internal var mTopAction: ((String) -> Unit)? = null
        internal var mMoreAction: ((String) -> Unit)? = null

        fun delete(action: (String) -> Unit) {
            mDeleteAction = action
        }

        fun top(action: (String) -> Unit) {
            mTopAction = action
        }

        fun more(action: (String) -> Unit) {
            mMoreAction = action
        }
    }

    /**
     * lateinit关键字:
     *              定义变量或者属性都是需要初始化值的，并且其都是private的，
     *              但是有些时候对于变量或者属性只需要声明，但是不需要初始化，
     *              则kotlin提供了lateinit关键字来实现
     *
     * 2、然后，在类中声明一个ListenerBuilder的实例引用，并且暴露一个设置该实例对象的一个方法，
     *    也就是我们常说的注册事件监听或回调的方法，类似setOnClickListenter这种。但是需要注意
     *    的是函数的参数是带ListenerBuilder返回值的lamba，类似下面这样:
     */
    private lateinit var mListener: ListenerBuilder
    fun addListener(listenerBuilder: ListenerBuilder.() -> Unit) { //带ListenerBuilder返回值的lamba
        mListener = ListenerBuilder().also(listenerBuilder)
    }

    /**
     * 3、最后在触发相应事件调用Builder实例中lamba即可
     */

    private fun addViewListener() {
        deleteTv.setOnClickListener {
            if (::mListener.isInitialized) {
                mListener.mDeleteAction?.invoke("DSL删除事件")
                closePopupWindow()
            }
        }
        topTv.setOnClickListener {
            if (::mListener.isInitialized) {
                mListener.mTopAction?.invoke("DSL置顶事件")
                closePopupWindow()
            }
        }
        moreTv.setOnClickListener {
            if (::mListener.isInitialized) {
                mListener.mMoreAction?.invoke("DSL更多事件")
                closePopupWindow()
            }
        }
    }
}