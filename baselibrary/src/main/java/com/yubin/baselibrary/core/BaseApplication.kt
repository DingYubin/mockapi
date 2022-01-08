package com.yubin.baselibrary.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.util.*

open class BaseApplication : Application(), ViewModelStoreOwner {

    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }
    private val mFactory: ViewModelProvider.Factory by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(this)
    }

    companion object {
        lateinit var sActivityLifeStackCallback: ActivityLifeStackCallback
        lateinit var context: BaseApplication
        lateinit var activityStack: LinkedList<Activity>
    }


    private fun getAppFactory(): ViewModelProvider.Factory {
        return mFactory
    }

    private fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        val app = checkApplication(activity) as BaseApplication
        return ViewModelProvider(app.viewModelStore, app.getAppFactory())
    }

    /**
     * 创建与APP生命周期一致的viewModel
     */
    fun <T : ViewModel?> getViewModel(activity: Activity, modelClass: Class<T>): T {
        return getAppViewModelProvider(activity).get(modelClass)
    }

    fun <T : ViewModel?> getViewModel(fragment: Fragment, modelClass: Class<T>): T {
        return getViewModel(fragment.requireActivity(), modelClass)
    }

    /**
     * 清理ViewModel缓存
     */
    fun clearViewModelStore() {
        viewModelStore.clear()
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity is not yet attached to the Application instance." +
                        "You can't request ViewModel before onCreate call."
            )
    }

    override fun onCreate() {
        super.onCreate()
        activityStack = LinkedList()
        sActivityLifeStackCallback =
            ActivityLifeStackCallback(activityStack)
        registerActivityLifecycleCallbacks(sActivityLifeStackCallback)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = this
    }

    override fun getViewModelStore(): ViewModelStore = appViewModelStore

    override fun getResources(): Resources { //还原字体大小

        val res: Resources = super.getResources()
        val configuration: Configuration = res.configuration
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f
            res.updateConfiguration(configuration, res.displayMetrics)
        }
        return res
    }
}