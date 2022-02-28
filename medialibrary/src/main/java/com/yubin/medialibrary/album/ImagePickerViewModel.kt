package com.yubin.medialibrary.album

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * description
 *
 * @author laiwei
 * @date create at 2019-07-15 15:08
 */
class ImagePickerViewModel(application: Application) :
    AndroidViewModel(application) {
    var folderMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var fetchMediaResult: MutableLiveData<String> = MutableLiveData()

}