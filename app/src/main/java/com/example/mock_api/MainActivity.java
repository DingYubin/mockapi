package com.example.mock_api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mock_api.api.UserApiService;
import com.example.mock_api.bean.User;
import com.yubin.httplibrary.mock_api.BaseObserver;
import com.yubin.httplibrary.mock_api.BaseResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.library.R.layout.activity_main);

        final TextView tvData = findViewById(R.id.data);

        UserApiService.getInstance(this).fetchUser(new BaseObserver<User>(){
            @Override
            public void onResponse(@NonNull BaseResponse<User> baseResponse, @Nullable User data) {
                if (data != null) {
                    tvData.setText(data.toString());
                }
            }
        });

//        UserApiService.getInstance(this).getAdBatch(new BaseObserver<AdBatch>(){
//            @Override
//            public void onResponse(@NonNull BaseResponse<AdBatch> baseResponse, @Nullable AdBatch data) {
//                if (baseResponse.getErrorCode() == 0 && data != null) {
//                    tvData.setText(data.toString());
//                }
//            }
//        });
    }
}