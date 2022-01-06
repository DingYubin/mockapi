package com.example.mock_api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yubin.account.user.ui.AccountActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.library.R.layout.activity_main);

        findViewById(R.id.goto_user_page).setOnClickListener(view -> {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        });
    }
}