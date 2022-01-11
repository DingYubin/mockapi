package com.yubin.mockapi.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.yubin.account.user.ui.AccountActivity;
import com.yubin.baselibrary.router.path.RouterPath;
import com.yubin.mockapi.R;
import com.yubin.mvp.ui.MvpLoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.goto_user_page).setOnClickListener(view -> {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.mvp).setOnClickListener(view -> {
            MvpLoginActivity.openLoginActivity(this);
        });

        findViewById(R.id.mvvm).setOnClickListener(view -> {
            ARouter.getInstance()
                    .build(RouterPath.AccountPage.PATH_MVVM_LOGIN)
                    .navigation();
        });

        findViewById(R.id.mvx).setOnClickListener(view -> {

            ARouter.getInstance()
                    .build(RouterPath.AccountPage.PATH_MVX_LOGIN)
                    .navigation();
        });
    }
}