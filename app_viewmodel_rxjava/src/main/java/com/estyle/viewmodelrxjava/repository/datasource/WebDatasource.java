package com.estyle.viewmodelrxjava.repository.datasource;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * httpservice的管理类
 */
public class WebDatasource {

    private static WebDatasource sInstance;
    private static Retrofit mRetrofit;

    private WebDatasource() {
    }

    public static WebDatasource getInstance() {
        if (sInstance == null) {
            synchronized (WebDatasource.class) {
                if (sInstance == null) {
                    sInstance = new WebDatasource();

                    mRetrofit = initRetrofit();
                }
            }
        }
        return sInstance;
    }

    private static Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://www.qubaobei.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public <T> T service(Class<T> service) {
        return mRetrofit.create(service);
    }
}
