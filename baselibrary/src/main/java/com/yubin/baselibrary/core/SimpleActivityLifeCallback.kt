package com.yubin.baselibrary.core

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

open class SimpleActivityLifeCallback : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //The subclass implementation
    }

    override fun onActivityStarted(activity: Activity) {
        //The subclass implementation
    }

    override fun onActivityResumed(activity: Activity) {
        //The subclass implementation
    }

    override fun onActivityPaused(activity: Activity) {
        //The subclass implementation
    }

    override fun onActivityStopped(activity: Activity) {
        //The subclass implementation
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        //The subclass implementation
    }

    override fun onActivityDestroyed(activity: Activity) {
        //The subclass implementation
    }
}