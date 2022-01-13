package com.yubin.mvx.databinding.dagger.viewmodel;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yubin.mvx.databinding.model.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * <pre>
 *     time   : 2018-1-16
 *     desc   : viewModel
 *     version: 1.0
 * </pre>
 */
@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @IntoMap
    @Binds
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindConversationModel(LoginViewModel loginViewModel);

}
