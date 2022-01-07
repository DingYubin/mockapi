package com.yubin.mvp.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.yubin.httplibrary.HttpClient;
import com.yubin.httplibrary.mock_api.BaseObserver;
import com.yubin.httplibrary.mock_api.CTHttpTransformer;


/**
 * Create yubin.ding on 21/7/18.
 */
public class LoginApiService {

	private static LoginApiService instance;

	private final LoginApi api;

	public static synchronized LoginApiService getInstance(Context context) {
		if (instance == null) {
			instance = new LoginApiService(HttpClient.getInstance(context).getClient().create(LoginApi.class));
		}
		return instance;
	}

	public LoginApiService(LoginApi api) {
		this.api = api;
	}

	public void login(String account, String password, BaseObserver<Object> observer) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("userLoginName", account);
		jsonObject.addProperty("password", password);
		api.login(jsonObject).compose(new CTHttpTransformer<>()).subscribe(observer);
	}

}
