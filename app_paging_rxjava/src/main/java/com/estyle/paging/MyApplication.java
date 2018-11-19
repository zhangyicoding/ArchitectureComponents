package com.estyle.paging;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhangyi on 2018/11/5.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRetrofit = initRetrofit();
    }

    private Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://www.qubaobei.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
