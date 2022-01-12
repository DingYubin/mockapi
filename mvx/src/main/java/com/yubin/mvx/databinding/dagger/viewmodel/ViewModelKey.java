package com.yubin.mvx.databinding.dagger.viewmodel;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

/**
 * <pre>
 *     @author : xiaoqing
 *     time   : 2018-1-16
 *     desc   :
 *     version: 1.0
 * </pre>
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
