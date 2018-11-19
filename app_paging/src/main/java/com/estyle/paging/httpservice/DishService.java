package com.estyle.paging.httpservice;


import com.estyle.paging.entity.DishEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhangyi on 2018/10/29.
 */
public interface DishService {

    @GET("ios/cf/dish_list.php?stage_id=1")
    Call<DishEntity> getDish(@Query("limit") int limit, @Query("page") int page);
}
