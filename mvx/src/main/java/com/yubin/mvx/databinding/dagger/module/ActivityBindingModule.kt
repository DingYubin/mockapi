package com.yubin.mvx.databinding.dagger.module

import com.yubin.mvx.databinding.ui.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * <pre>
 * time   : 2018-1-16
 * desc   : ActivityModule
 * version: 1.0
</pre> *
 */
@Module
interface ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [])
    fun contributeLoginActivity(): LoginActivity

    //    SplashActivity contributeSplashActivity();
    //
    //    @ContributesAndroidInjector(modules = di.FragmentModule.class)
    //    MainActivity contributeMainActivity();
}