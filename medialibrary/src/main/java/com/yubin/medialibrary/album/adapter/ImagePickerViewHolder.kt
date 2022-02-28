package com.yubin.medialibrary.album.adapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.ImageSelectListener
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.extra.BaseViewHolder


/**
 * description
 *
 * @author laiwei
 * @date create at 2019-07-08 18:05
 */
class ImagePickerViewHolder(
    itemView: View,
    private val mPickerAdapter: ImagePickerAdapter
) :
    BaseViewHolder<ImagePickerBean?>(itemView) {
    private var mImageUrlView: AppCompatImageView = itemView.findViewById(R.id.adapter_item_image)
    private var mImageSelectState: AppCompatTextView =
        itemView.findViewById(R.id.adapter_item_select_state)
    private var mImageLayer: View = itemView.findViewById(R.id.adapter_item_layer)
    private var mVideoDuration: AppCompatTextView = itemView.findViewById(R.id.adapter_video_time)
    private var mSelectListener: ImageSelectListener? = null
    override fun bindDataFully(data: ImagePickerBean, position: Int, count: Int) {
        if (data.isVideo) {
            mVideoDuration.visibility = View.VISIBLE
            mVideoDuration.text = data.showTime
            ImageLoaderManager.INSTANCE.intoTarget(data.thumbData, data.data, mImageUrlView)
        } else {
            mVideoDuration.visibility = View.GONE
            ImageLoaderManager.INSTANCE.intoTarget(data.data, mImageUrlView)
        }
        setImageSelectState(data)
        /*
         *选中图片和反选
         */
        mImageUrlView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val originSelect = data.isSelect
                if (mPickerAdapter.getSelectIndex() >= data.maxSelectNumber && !originSelect) {
                    MediaManager.instance.toast.invoke(
                        "最多只能选择" + data.maxSelectNumber + "张照片噢～",
                        mImageUrlView.context
                    )
                    return
                }
                if (data.isVideo && data.size > data.maxVideoSize) {
                    val sizeByM = data.maxVideoSize / 1024 / 1024
                    MediaManager.instance.toast.invoke(
                        "视频过大(超过${sizeByM}MB)，暂时无法发送",
                        mImageUrlView.context
                    )
                    return
                }
                mPickerAdapter.setSelectIndex(
                    mSelectListener!!.onImageSelected(
                        data,
                        !originSelect
                    )
                )
                data.isSelect = !originSelect
                if (!originSelect) {
                    if (data.maxSelectNumber == mPickerAdapter.getSelectIndex()) {
                        mPickerAdapter.notifyDataSetChanged()
                    } else {
                        setImageSelectState(data)
                    }
                } else {
                    mPickerAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun bindDiffData(
        data: ImagePickerBean,
        payload: Bundle,
        position: Int,
        count: Int
    ) {
        //bindDiffData
    }

    /**
     * 设置item项的状态，选中，未选中，已经选择完成
     */
    private fun setImageSelectState(data: ImagePickerBean) {
        if (data.isSelect) {
            //选中状态
            mImageSelectState.text = data.badge.toString()
            MediaManager.instance.mediaStyle?.let {
                mImageSelectState.setBackgroundResource(it.imagePickerSelect)
            }
            mImageLayer.visibility = View.GONE
        } else {
            data.badge = 0
            mImageSelectState.text = ""
            MediaManager.instance.mediaStyle?.let {
                mImageSelectState.setBackgroundResource(it.imagePickerUnselect)
            }
            if (mPickerAdapter.getSelectIndex() >= data.maxSelectNumber) {
                //已经超过选择最大数量，需要蒙层
                mImageLayer.visibility = View.VISIBLE
            } else {
                mImageLayer.visibility = View.GONE
            }
        }
    }

    fun setImageSelectListener(listener: ImageSelectListener?) {
        mSelectListener = listener
    }

}