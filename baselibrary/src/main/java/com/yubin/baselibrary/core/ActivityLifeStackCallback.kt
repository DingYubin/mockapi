package com.yubin.baselibrary.core

import android.app.Activity
import android.os.Bundle
import java.util.*

class ActivityLifeStackCallback internal constructor(private val activityStack: LinkedList<Activity>) :
    SimpleActivityLifeCallback() {
    private val resumedActivity: MutableList<Activity> = ArrayList()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        resumedActivity.add(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        resumedActivity.remove(activity)
    }

    fun getResumedActivity(): List<Activity> {
        return resumedActivity
    }
}