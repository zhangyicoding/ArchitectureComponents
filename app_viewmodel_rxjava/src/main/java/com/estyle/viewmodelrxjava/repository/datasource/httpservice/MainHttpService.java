package com.estyle.viewmodelrxjava.repository.datasource.httpservice;

import com.estyle.viewmodelrxjava.bean.DishBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit的接口，通过它最终获取数据
 */
public interface MainHttpService {

    @GET("ios/cf/dish_list.php?stage_id=1&limit=20")
    Observable<DishBean> mainObservable(@Query("page") int page);
}
