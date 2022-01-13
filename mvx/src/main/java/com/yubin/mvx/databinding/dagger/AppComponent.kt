package com.yubin.mvx.databinding.dagger

import android.app.Application
import com.yubin.mvx.databinding.dagger.data.DbModule
import com.yubin.mvx.databinding.dagger.data.RepositoryModule
import com.yubin.mvx.databinding.dagger.module.ActivityModule
import com.yubin.mvx.databinding.dagger.module.ApplicationModule
import com.yubin.mvx.databinding.dagger.module.ServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * <pre>
 * time    : 2018/1/8
 * desc    : 组件定义
 * version : 1.0
</pre> *
 */
@Singleton
@Component(modules = [ApplicationModule::class,
    RepositoryModule::class,
    DbModule::class,
    ActivityModule::class,
    ServiceModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(app: Application)

    //    void inject(AppModuleBean appModuleBean);
    //
    //    void inject(AppTraceRepository appTraceRepository);
}