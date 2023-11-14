package com.yubin.draw.widget.view.text

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.DynamicDrawableSpan
import android.widget.EditText
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.draw.R
import com.yubin.draw.bean.MemberBean


/**
 * Created by Hsu on 2015/10/14.
 */
class RemindHandler(private val EDITOR: EditText) : TextWatcher {
    val REMINDS: ArrayList<RemindDynamicDrawableSpan> = ArrayList()
    private lateinit var mOnSpanDeletedListener: OnSpanChangedListener

    init {
        EDITOR.addTextChangedListener(this)
    }

    val reminds: Array<RemindDynamicDrawableSpan>
        get() = EDITOR.text
            .getSpans(
                0,
                EDITOR.text.length,
                RemindDynamicDrawableSpan::class.java
            )

    fun insert(dragContext: String) {
        val start = EDITOR.selectionStart
        val end = EDITOR.selectionEnd
        val message = EDITOR.editableText
        message.insert(end, dragContext)
    }

    fun insert(nickName: String, uid: String) {
        val span: RemindDynamicDrawableSpan =
            RemindDynamicDrawableSpan(
                uid,
                nickName,
                EDITOR.context
            )
        val start = EDITOR.selectionStart
        var end = EDITOR.selectionEnd
        val message = EDITOR.editableText
        message.replace(start, end, nickName)
        end = start + nickName.length
        span.start = start
        message.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (::mOnSpanDeletedListener.isInitialized) {
            val userInfo = MemberBean()
            userInfo.id = span.uid
            val nickname: String = span.nickname
            userInfo.name = nickname.substring(1)
            mOnSpanDeletedListener.mSpanAdd?.invoke(userInfo, REMINDS.size)
        }
        message.insert(end, " ")
    }

    override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
        if (count > 0) {
            val end = start + count
            val message = EDITOR.editableText
            val list: Array<RemindHandler.RemindDynamicDrawableSpan> =
                message.getSpans(
                    start, end,
                    RemindHandler.RemindDynamicDrawableSpan::class.java
                )
            for (span in list) {
                val spanStart = message.getSpanStart(span)
                val spanEnd = message.getSpanEnd(span)
                if (spanStart < end && spanEnd > start) {
                    span.start = spanStart
                    span.end = spanEnd
                    REMINDS.add(span)
                }
            }
        }
    }

    override fun afterTextChanged(text: Editable) {
        val message = EDITOR.editableText
        for (i in REMINDS.indices) {
            val span: RemindHandler.RemindDynamicDrawableSpan = REMINDS[i]
            val start = message.getSpanStart(span)
            val end = message.getSpanEnd(span)

            if (::mOnSpanDeletedListener.isInitialized) {
                val userInfo = MemberBean()
                userInfo.id = span.uid
                val nickname: String = span.nickname
                userInfo.name = nickname.substring(1)
                mOnSpanDeletedListener.mSpanDeleted?.invoke(userInfo, i)
            }
            message.removeSpan(span)
            if (start != end) {
                message.delete(start, end)
            }
        }
        REMINDS.clear()
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {}

    inner class RemindDynamicDrawableSpan(
        var uid: String,
        val nickname: String,
        private val context: Context
    ) : DynamicDrawableSpan() {
        var start = 0
        var end = 0

        override fun getDrawable(): Drawable {
            val bitmap = nameBitmap
            val drawable = BitmapDrawable(
                context.resources, bitmap
            )
            drawable.setBounds(
                0, 0,
                bitmap.width,
                bitmap.height
            )
            return drawable
        }

        private val nameBitmap: Bitmap
            get() {
                val paint = Paint()
                paint.isAntiAlias = true
                paint.textSize = context.resources.getDimension(R.dimen.chat_font_normal_size) - 1
                paint.color = Color.parseColor("#008CF5")
                val rect = Rect()
                paint.getTextBounds(nickname, 0, nickname.length, rect)
                val padding: Int = 1.dp
                // 获取字符串在屏幕上的长度
                val width = paint.measureText(nickname).toInt()
                val bmp = Bitmap.createBitmap(
                    width + padding * 2, rect.height() + padding * 2,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bmp)
                canvas.drawColor(Color.WHITE)
                val w = Paint()
                w.color = Color.TRANSPARENT
                canvas.drawRect(
                    1f,
                    0f,
                    (rect.right + padding * 2).toFloat(),
                    (rect.height() + padding * 2).toFloat(),
                    w
                )
                canvas.drawText(
                    nickname,
                    (rect.left + padding).toFloat(),
                    (rect.height() + padding - rect.bottom).toFloat(),
                    paint
                )
                return bmp
            }
    }


    fun addOnSpanChangedListener(mOnSpanDeletedListener: OnSpanChangedListener.() -> Unit) {
        this.mOnSpanDeletedListener = OnSpanChangedListener().also(mOnSpanDeletedListener)
    }

    inner class OnSpanChangedListener {
        internal var mSpanDeleted: ((MemberBean, Int) -> Unit)? = null
        internal var mSpanAdd: ((MemberBean, Int) -> Unit)? = null

        fun delete(action: (MemberBean, Int) -> Unit) {
            mSpanDeleted = action
        }

        fun add(action: (MemberBean, Int) -> Unit) {
            mSpanAdd = action
        }
    }
}