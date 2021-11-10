package com.example.mock_api.api;

import com.example.mock_api.bean.AdBatch;
import com.example.mock_api.bean.User;

import retrofit2.http.GET;
import io.reactivex.Observable;

/**
 * Create yubin.ding@casstime.com on 21/7/18.
 */
public interface UserApi {

	@GET("/api/user")
	Observable<BaseResponse<User>> fetchUser();

	@GET("/api/adv")
	Observable<BaseResponse<AdBatch>> getAdBatch();

}
