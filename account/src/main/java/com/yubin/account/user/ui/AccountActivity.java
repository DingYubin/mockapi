package com.yubin.account.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.yubin.account.R;
import com.yubin.account.user.api.UserApiService;
import com.yubin.account.user.bean.AdBatch;
import com.yubin.account.user.bean.User;
import com.yubin.httplibrary.mock_api.BaseObserver;
import com.yubin.httplibrary.mock_api.BaseResponse;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initViews();
    }

    private void initViews() {
        final AppCompatTextView tvUserData = findViewById(R.id.user_text);
        final AppCompatTextView tvAdData = findViewById(R.id.ad_text);
        final AppCompatButton BtnUser = findViewById(R.id.user_btn);

        BtnUser.setOnClickListener(view -> UserApiService.getInstance(this).fetchUser(new BaseObserver<User>(){
            @Override
            public void onResponse(@NonNull BaseResponse<User> baseResponse, @Nullable User data) {
                if (data != null) {
                    tvUserData.setText(data.toString());
                }
            }
        }));

        UserApiService.getInstance(this).getAdBatch(new BaseObserver<AdBatch>(){
            @Override
            public void onResponse(@NonNull BaseResponse<AdBatch> baseResponse, @Nullable AdBatch data) {
                if (baseResponse.getErrorCode() == 0 && data != null) {
                    tvAdData.setText(data.toString());
                }
            }
        });
    }
}
