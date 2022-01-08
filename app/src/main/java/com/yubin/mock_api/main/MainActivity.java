package com.yubin.mock_api.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mock_api.R;
import com.yubin.account.user.ui.AccountActivity;
import com.yubin.mvp.ui.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.library.R.layout.activity_main);

        findViewById(R.id.goto_user_page).setOnClickListener(view -> {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.mvp).setOnClickListener(view -> {
            LoginActivity.openLoginActivity(this);
        });
    }
}