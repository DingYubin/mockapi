package com.yubin.medialibrary.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * description
 *
 * @author laiwei
 * @date create at 2019-07-15 15:08
 * email wei.lai
 */
class CameraViewModel(application: Application) :
    AndroidViewModel(application) {

    var fetchMediaResult: MutableLiveData<Bitmap> = MutableLiveData()

}