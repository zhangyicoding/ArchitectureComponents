package com.estyle.viewmodel.repository.datasource.httpservice;

import com.estyle.viewmodel.bean.DishBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit的接口，通过它最终获取数据
 */
public interface MainHttpService {

    @GET("ios/cf/dish_list.php?stage_id=1&limit=20")
    Call<DishBean> mainCall(@Query("page") int page);
}
