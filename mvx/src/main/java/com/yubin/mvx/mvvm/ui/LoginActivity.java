package com.yubin.mvx.mvvm.ui;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yubin.baselibrary.router.path.RouterPath;
import com.yubin.mvx.R;
import com.yubin.mvx.base.BaseActivity;
import com.yubin.mvx.databinding.ActivityLoginNewBinding;
import com.yubin.mvx.mvvm.model.LoginViewModel;

@Route(path = RouterPath.AccountPage.PATH_MVX_LOGIN)
public class LoginActivity extends BaseActivity<ActivityLoginNewBinding> {

    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    protected void initViewModel() {
        loginViewModel = getViewModel(LoginViewModel.class);
        bindingView.setModel(loginViewModel);
    }

    @Override
    protected void init() {

        bindingView.password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        bindingView.login.setOnClickListener(view -> {

            Log.d("MvpLoginActivity", "account : " + bindingView.account.getText().toString() + ", password : " + bindingView.password.getText().toString());
        });

        bindingView.returnButton.setOnClickListener(view -> {
            finish();
        });

    }


    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
