package com.example.mock_api.api;

import android.content.Context;

import com.example.mock_api.bean.AdBatch;
import com.example.mock_api.bean.User;
import com.yubin.httplibrary.HttpClient;
import com.yubin.httplibrary.mock_api.BaseObserver;
import com.yubin.httplibrary.mock_api.CTHttpTransformer;

/**
 * Create yubin.ding on 21/7/18.
 */
public class UserApiService {

	private static UserApiService instance;

	private final UserApi api;

	public static synchronized UserApiService getInstance(Context context) {
		if (instance == null) {
			instance = new UserApiService(HttpClient.getInstance(context).getClient().create(UserApi.class));
		}
		return instance;
	}

	public UserApiService(UserApi api) {
		this.api = api;
	}

	public void fetchUser(BaseObserver<User> observer) {
		api.fetchUser().compose(new CTHttpTransformer<>()).subscribe(observer);
	}

	public void getAdBatch(BaseObserver<AdBatch> observer) {
		api.getAdBatch().compose(new CTHttpTransformer<>()).subscribe(observer);
	}
}
