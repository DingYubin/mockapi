package com.example.mock_api.api;

import android.content.Context;


import com.example.mock_api.HttpClient;
import com.example.mock_api.User;

/**
 * Create kang.zhou@shanbay.com on 16/8/18.
 */
public class UserApiService {

	private static UserApiService instance;

	private UserApi api;

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
}
