package com.yubin.mvx.databinding.dagger.viewmodel;


import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

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

//    @IntoMap
//    @Binds
//    @ViewModelKey(ConversationModel.class)
//    abstract ViewModel bindConversationModel(ConversationModel conversationModel);

}
