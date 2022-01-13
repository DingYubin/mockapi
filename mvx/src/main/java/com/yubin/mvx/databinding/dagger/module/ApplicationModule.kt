package com.yubin.mvx.databinding.dagger.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/**
 * <pre>
 * time    : 2018/1/8
 * desc    : Application bind
 * version : 1.0
</pre> *
 */
@Module
interface ApplicationModule {
    @Binds
    fun bindContext(application: Application?): Context?
}