package com.example.mock_api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mock_api.api.BaseObserver;
import com.example.mock_api.api.BaseResponse;
import com.example.mock_api.api.CTHttpTransformer;
import com.example.mock_api.api.UserApiService;

import org.jetbrains.annotations.NotNull;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView tvData = findViewById(R.id.data);

        UserApiService.getInstance(this).fetchUser(new BaseObserver<User>(){
            @Override
            public void onResponse(@NonNull BaseResponse<User> baseResponse, @Nullable User data) {
                tvData.setText(data.username);
            }
        });
    }




}