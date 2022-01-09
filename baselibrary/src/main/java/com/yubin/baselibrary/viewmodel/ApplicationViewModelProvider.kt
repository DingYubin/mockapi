package com.yubin.baselibrary.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.yubin.baselibrary.core.BaseApplication

/**
 * 全局的AndroidViewModel提供者, 提供一个生命周期和Application一样的ViewModel
 */
class ApplicationViewModelProvider(owner: ViewModelStoreOwner, factory: Factory) :
    ViewModelProvider(owner, factory) {

    companion object {

        fun getInstance(): ApplicationViewModelProvider = ApplicationViewModelProvider(
            BaseApplication.context,
            AndroidViewModelFactory.getInstance(BaseApplication.context)
        )
    }

}