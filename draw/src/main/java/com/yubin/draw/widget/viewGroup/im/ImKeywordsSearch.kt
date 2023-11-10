package com.yubin.draw.widget.viewGroup.im

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.yubin.baselibrary.impl.DebounceOnClickListener
import com.yubin.draw.R

class ImKeywordsSearch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var mKeywordsBarInput: CustomAppCompatEditText? = null

    private var mKeywordsBarClear: AppCompatImageView? = null

    private var mClearListen: DebounceOnClickListener? = null

    private var mKeywordsChangedListener: ((str: String) -> Unit)? = null

    private var isPaste = false

    init {
        inflateContentViewWithContext()
    }

    private fun inflateContentViewWithContext() {
        inflate(context, R.layout.im_widget_keywords_search, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mKeywordsBarInput = findViewById(R.id.cec_im_keywords_input)
        mKeywordsBarClear = findViewById(R.id.cec_im_keywords_clear)

        this.setStateAndListener()
    }

    /**
     * 设置文本变化监听和点击事件
     */
    private fun setStateAndListener() {

        mKeywordsBarInput?.addTextContextMenuListener { id ->
            if (id == android.R.id.paste) {
                isPaste = true
            }
        }

        mKeywordsBarInput?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //do nothing because of
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    mKeywordsBarClear!!.visibility = GONE
                    mKeywordsChangedListener?.invoke("")
                } else if (isPaste) {
                    isPaste = false
                    val result = s.toString().replace("\r|\n|\t".toRegex(), "").trim { it <= ' ' }
                    mKeywordsBarInput?.setText(result)
                    mKeywordsBarInput?.setSelection(result.length)
                } else {
                    if (s.toString().contains("\n")) {
                        //处理从剪贴板直接选择内容，剔除空格
                        val result = s.toString().replace("\n".toRegex(), "").trim { it <= ' ' }
                        mKeywordsBarInput!!.setText(result)
                        mKeywordsBarInput!!.setSelection(result.length)
                        return
                    }

                    mKeywordsBarClear?.visibility = VISIBLE
                    mKeywordsChangedListener?.invoke(s.toString())
                }
            }
        })

        /*
         *  清除搜索框
         */
        mKeywordsBarClear?.setOnClickListener(object : DebounceOnClickListener() {
            override fun doClick(v: View?) {
                mKeywordsBarInput?.setText("")
                mClearListen?.doClick(v)
            }
        })
    }

    fun setKeywordsChangedListener(listener: (str: String) -> Unit) {
        this.mKeywordsChangedListener = listener
    }

    /**
     * 设置清除关键字事件
     */
    fun setSearchBarKeywordsClearListener(listener: DebounceOnClickListener?) {
        mClearListen = listener
    }

    /**
     * 获取搜索关键字
     */
    fun getSearchBarKeywords(): String {
        return if (mKeywordsBarInput?.text == null) "" else mKeywordsBarInput?.text.toString()
            .trim()
    }

}