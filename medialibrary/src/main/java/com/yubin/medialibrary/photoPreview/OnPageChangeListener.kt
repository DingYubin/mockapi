package com.yubin.medialibrary.photoPreview

import android.util.Log
import androidx.viewpager.widget.ViewPager

/**
 * @description:
 * <p>
 * @author: 孙莹莹(A01266)
 * <p>
 * @date: 2021/3/1 11:13 AM
 */
open class OnPageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
        Log.d("CMOnPageChangeListener", "onPageScrollStateChanged")
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Log.d("CMOnPageChangeListener", "onPageScrolled")
    }

    override fun onPageSelected(position: Int) {
        Log.d("CMOnPageChangeListener", "onPageSelected")
    }

}