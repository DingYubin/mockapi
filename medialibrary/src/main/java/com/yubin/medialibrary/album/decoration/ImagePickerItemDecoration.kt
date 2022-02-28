package com.yubin.medialibrary.album.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yubin.medialibrary.util.UnitHelper

/**
 *description
 *
 *@author laiwei
 *@date create at 4/25/21 1:51 PM
 */
class ImagePickerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private var left0: Int = 0
    private var left1: Int = 0
    private var left2: Int = 0
    private var left3: Int = 0

    init {
        val space = (context.resources?.displayMetrics?.widthPixels?.minus(
            UnitHelper.dp2px(
                context,
                91 * 4f
            )
        ))
        if (space != null) {
            left0 = (space / 5).toInt()
            left1 = (space * 3 / 20).toInt()
            left2 = (space * 2 / 20).toInt()
            left3 = (space / 20).toInt()
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.adapter == null) {
            return
        }
        val position = parent.getChildAdapterPosition(view)
        outRect.bottom = left0
        if (position <= 3) {
            outRect.top = left0
        }
        val ret = position % 4
        if (ret == 0) {
            outRect.left = left0
        } else if (ret == 1) {
            outRect.left = left1
        } else if (ret == 2) {
            outRect.left = left2
        } else if (ret == 3) {
            outRect.left = left3
        }
    }

}