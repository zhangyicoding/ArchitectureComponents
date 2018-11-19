package com.estyle.paging.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.arch.paging.RxPagedListBuilder;

import com.estyle.paging.datasource.DishDataSource;
import com.estyle.paging.entity.DishEntity;

import io.reactivex.Observable;

/**
 * Created by zhangyi on 2018/11/5.
 */
public class DishViewModel extends ViewModel {

    private DishDataSource.Factory mDataSourceFactory;

    private Observable<PagedList<DishEntity.DataEntity>> mList;
    private LiveData<Boolean> mIsRefreshing;

    public DishViewModel() {
        mDataSourceFactory = new DishDataSource.Factory();

        mList = new RxPagedListBuilder<>(
                mDataSourceFactory,
                new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10)
                        .setPrefetchDistance(2)
                        .build()
        ).buildObservable();

        mIsRefreshing = Transformations.switchMap(mDataSourceFactory.getDataSource(), new Function<DishDataSource, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(DishDataSource dataSource) {
                return dataSource.isRefreshing();
            }
        });
    }

    public void refresh() {
        mDataSourceFactory.getDataSource().getValue().invalidate();
    }

    public LiveData<Boolean> getIsRefreshing() {
        return mIsRefreshing;
    }

    public Observable<PagedList<DishEntity.DataEntity>> getList() {
        return mList;
    }
}
