package com.estyle.viewmodelrxjava.repository;


import com.estyle.viewmodelrxjava.bean.DishBean;
import com.estyle.viewmodelrxjava.repository.datasource.WebDatasource;
import com.estyle.viewmodelrxjava.repository.datasource.httpservice.MainHttpService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Model层
 * 向Presenter层提供数据
 * 这是统一的数据提供层，但数据来源不同，该层会直接联系到不同的数据源，
 * 例如：网络数据联系到WebDatasource，DB数据联系到DBDatasource，内存数据直接产生，文件、SP等联系到相关数据源
 */
public class MainListRepository {

    // 获取主界面
    public Observable<List<DishBean.DataBean>> getMainDataFromWeb(int page) {
        return WebDatasource.getInstance()
                .service(MainHttpService.class)
                .mainObservable(page)
                .subscribeOn(Schedulers.io())
                .map(new Function<DishBean, List<DishBean.DataBean>>() {
                    @Override
                    public List<DishBean.DataBean> apply(DishBean dishBean) throws Exception {
                        return dishBean.getData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
