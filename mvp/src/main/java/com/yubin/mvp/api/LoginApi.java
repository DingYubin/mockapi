package com.yubin.mvp.api;

import com.google.gson.JsonObject;
import com.yubin.httplibrary.mock_api.BaseResponse;
import com.yubin.httplibrary.mock_api.None;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Create yubin.ding on 21/7/18.
 */
public interface LoginApi {

	@POST("/api/login")
	Observable<BaseResponse<None>> login(@Body JsonObject body);

}
