package com.yubin.mvp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.yubin.mvp.R;
import com.yubin.mvp.interfaces.LoginInterface;
import com.yubin.mvp.presenter.LoginPresenter;

public class MvpLoginActivity extends AppCompatActivity implements LoginInterface.View {
    private LoginInterface.Presenter presenter;

    public static void openLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, MvpLoginActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter = new LoginPresenter(this, this);
        initView();
    }

    private void initView() {

        AppCompatEditText account = findViewById(R.id.et_connection);
        AppCompatEditText password = findViewById(R.id.et_password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        TextView btnLogin = findViewById(R.id.btn_login);
        btnLogin.setEnabled(true);

        btnLogin.setOnClickListener(view -> {

            Log.d("LoginActivity", "account : " + account.getText().toString() + ", password : " + password.getText().toString());
            presenter.login(account.getText().toString(), password.getText().toString());
        });

        findViewById(R.id.return_button).setOnClickListener(view -> {
            finish();
        });
    }


    @Override
    public void onSuccess() {
        showMsg("登陆成功");
        finish();
    }

    @Override
    public void onFailed() {
        showMsg("登陆失败");
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
