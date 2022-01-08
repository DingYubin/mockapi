package com.yubin.account.user.api;

import com.yubin.account.user.bean.AdBatch;
import com.yubin.account.user.bean.User;
import com.yubin.httplibrary.mockapi.BaseResponse;

import retrofit2.http.GET;
import io.reactivex.Observable;

/**
 * Create yubin.ding on 21/7/18.
 */
public interface UserApi {

	@GET("/api/user")
	Observable<BaseResponse<User>> fetchUser();

	@GET("/api/adv")
	Observable<BaseResponse<AdBatch>> getAdBatch();

}
