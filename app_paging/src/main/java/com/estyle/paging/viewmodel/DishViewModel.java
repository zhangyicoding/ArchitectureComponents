package com.estyle.paging.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.estyle.paging.datasource.DishDataSource;
import com.estyle.paging.entity.DishEntity;

/**
 * Created by zhangyi on 2018/11/5.
 */
public class DishViewModel extends ViewModel {

    private DishDataSource.Factory mDataSourceFactory;

    private LiveData<PagedList<DishEntity.DataEntity>> mList;
    private LiveData<Boolean> mIsRefreshing;
    private LiveData<String> mErrorMsg;

    public DishViewModel() {
        mDataSourceFactory = new DishDataSource.Factory();

        mIsRefreshing = Transformations.switchMap(mDataSourceFactory.getDataSource(), new Function<DishDataSource, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(DishDataSource dataSource) {
                return dataSource.isRefreshing();
            }
        });

        mErrorMsg = Transformations.switchMap(mDataSourceFactory.getDataSource(), new Function<DishDataSource, LiveData<String>>() {
            @Override
            public LiveData<String> apply(DishDataSource dataSource) {
                return dataSource.mErrorMsg;
            }
        });
    }

    public void refresh() {
        if (mList == null) {
            mList = buildList();
        } else {
            mDataSourceFactory.getDataSource().getValue().invalidate();
        }
    }

    public void retry() {
        mDataSourceFactory.getDataSource().getValue().mRetry.getValue().run();
    }

    private LiveData<PagedList<DishEntity.DataEntity>> buildList() {
        return new LivePagedListBuilder<>(
                mDataSourceFactory,
                new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)// 加载初始页的item数量
                        .setPageSize(10)// 加载之后每页的item数量
                        .setPrefetchDistance(2)// 预加载距离，单位没有明确说明，可能是item的数量
                        .build()
        ).build();
    }

    public LiveData<Boolean> getIsRefreshing() {
        return mIsRefreshing;
    }

    public LiveData<String> getErrorMsg() {
        return mErrorMsg;
    }

    public LiveData<PagedList<DishEntity.DataEntity>> getList() {
        return mList;
    }
}
