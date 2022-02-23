package com.yubin.medialibrary.manager

/**
 *description 可配置的ui样式
 *
 *@author laiwei
 *@date create at 4/24/21 4:27 PM
 */
data class MediaStyle(
    //发送按钮颜色
    var sendTextColor: Int,
    //发送按钮背景图
    var sendTextBg: Int,
    //相册图片选中背景
    var imagePickerSelect: Int,
    //相册图片未选中
    var imagePickerUnselect: Int,
    //默认占位图
    var defaultImage: Int,
    //预览选中图片背景
    var previewSelectImage: Int,
    //预览右上角文字数量背景
    var previewLabelImage: Int,
    //取消拍摄按钮颜色
    var cancelBtnColor: Int,
    //拍摄进度圆圈颜色
    var recordBtnColor: Int,
    //相册文件夹选中图片
    var folderSelectImage: Int
)
