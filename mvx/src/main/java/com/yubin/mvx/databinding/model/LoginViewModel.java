package com.yubin.mvx.databinding.model;

import android.app.Application;

import androidx.databinding.ObservableField;

import com.yubin.mvx.databinding.base.BaseViewModel;

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
