package com.estyle.viewmodel.repository;


import com.estyle.viewmodel.bean.DishBean;
import com.estyle.viewmodel.repository.datasource.WebDatasource;
import com.estyle.viewmodel.repository.datasource.httpservice.MainHttpService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Model层
 * 向Presenter层提供数据
 * 这是统一的数据提供层，但数据来源不同，该层会直接联系到不同的数据源，
 * 例如：网络数据联系到WebDatasource，DB数据联系到DBDatasource，内存数据直接产生，文件、SP等联系到相关数据源
 */
public class MainListRepository {

    private com.estyle.viewmodel.viewmodel.Callback<List<DishBean.DataBean>> mCallback;

    public MainListRepository(com.estyle.viewmodel.viewmodel.Callback<List<DishBean.DataBean>> callback) {
        mCallback = callback;
    }

    // 获取主界面
    public void getMainDataFromWeb(int page) {
        WebDatasource.getInstance()
                .service(MainHttpService.class)
                .mainCall(page)
                .enqueue(new Callback<DishBean>() {
                    @Override
                    public void onResponse(Call<DishBean> call, Response<DishBean> response) {
                        mCallback.onSuccess(response.body().getData());
                    }

                    @Override
                    public void onFailure(Call<DishBean> call, Throwable t) {
                        mCallback.onFailure(t);
                    }
                });
    }
}
