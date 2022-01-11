package com.yubin.mvx.mvvm.model;

import android.app.Application;

import androidx.databinding.ObservableField;

import com.yubin.mvx.base.BaseViewModel;

public class LoginViewModel extends BaseViewModel {

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> pwd = new ObservableField<>("");

    public LoginViewModel(Application application) {
        super(application);
    }

    public void clearName() {
        name.set("");
    }
}
