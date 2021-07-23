package com.example.mock_api.api;

import com.example.mock_api.User;

import retrofit2.http.GET;
import io.reactivex.Observable;

/**
 * Create kang.zhou@shanbay.com on 16/8/18.
 */
public interface UserApi {

	@GET("/api/user")
	Observable<BaseResponse<User>> fetchUser();

}
