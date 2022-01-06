package com.yubin.httplibrary;

import android.content.Context;
import android.text.TextUtils;

import com.yubin.library.mock.MockApiInterceptor;
import com.yubin.library.mock.MockApiSuite;
import com.yubin.library.mock.api.StandardMockApi;
import com.yubin.library.mock.constant.MockHttpMethod;
import com.google.gson.Gson;
import com.yubin.httplibrary.model.MockConfig;
import com.yubin.httplibrary.util.ToolUtil;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create yubin.ding.com on 21/7/18.
 */
public class HttpClient {

	public static String BASE_API_URL = "http://www.yubin.com/";

	private static HttpClient sInstance;

	private Retrofit retrofit;

	public static synchronized HttpClient getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new HttpClient();

			// mock 数据默认存放在assets目录中，如果需要放到sdcard上，使用new MockApiInterceptor(context, true)
			MockApiInterceptor mockApiInterceptor = new MockApiInterceptor(context);

			String mockJson = ToolUtil.stringFromAssets(context, "config.json");
			if (!TextUtils.isEmpty(mockJson)){
				MockConfig mockConfig = new Gson().fromJson(mockJson, MockConfig.class);
				for (MockConfig.Mocks mocks : mockConfig.getMocks()) {
					MockApiSuite suite = new MockApiSuite(mocks.getName()); // account为suite name
					for (MockConfig.Mocks.Mock mock : mocks.getMock()) {
						suite.addMockApi(new StandardMockApi(MockHttpMethod.GET, mock.getApi()).setSuccessDataFile(mock.getMockFile()));
					}
					mockApiInterceptor.addMockApiSuite(suite);
				}
			}

			// define client
			OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(mockApiInterceptor);

			sInstance.retrofit = new Retrofit.Builder()
					.baseUrl(BASE_API_URL)
					.client(clientBuilder.build())
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.build();
		}
		return sInstance;
	}

	public Retrofit getClient() {
		return retrofit;
	}
}
