package com.yubin.mvx.databinding.dagger.data;

import com.yubin.mvx.databinding.dagger.viewmodel.ViewModelModule;

import dagger.Module;

/**
 * <pre>
 *     @author : xiaoqing
 *     desc    :
 *     version : 1.0
 * </pre>
 */
@Module(includes = {ApiModule.class, ViewModelModule.class})
public class RepositoryModule {

//    @Singleton
//    @Provides
//    static UserLocalData provideUserLocal(AppExecutors appExecutors) {
//        return new UserLocalData(appExecutors);
//    }
//
//    @Singleton
//    @Provides
//    static UserRemoteData provideUserRemote(Api api, AppExecutors appExecutors) {
//        return new UserRemoteData(api, appExecutors);
//    }
//
//    @Singleton
//    @Provides
//    static AppExecutors provideExecutor() {
//        return new AppExecutors(Executors.newSingleThreadExecutor(),
//                RxUtil.netWorkExecutor,
//                new AppExecutors.MainThreadExecutor());
//    }
}
