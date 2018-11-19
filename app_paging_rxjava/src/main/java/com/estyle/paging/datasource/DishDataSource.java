package com.estyle.paging.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.estyle.paging.MyApplication;
import com.estyle.paging.entity.DishEntity;
import com.estyle.paging.httpservice.DishService;
import com.estyle.paging.zhangyi.ZYLog;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class DishDataSource extends PageKeyedDataSource<Integer, DishEntity.DataEntity> {

    private static final String TAG = DishDataSource.class.getSimpleName();

    private MutableLiveData<Boolean> mIsRefreshing = new MutableLiveData<>();// 刷新状态

    public MutableLiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadInitial, page size: " + params.requestedLoadSize);
        isRefreshing().postValue(true);
        loadData(params.requestedLoadSize, 1, callback, null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadBefore, thread: " + Thread.currentThread().getName() + ", size: " + params.requestedLoadSize + ", key: " + params.key);
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadAfter,  size: " + params.requestedLoadSize + ", key: " + params.key);
        loadData(params.requestedLoadSize, params.key, null, callback);
    }

    private void loadData(int limit, final int page, final LoadInitialCallback<Integer, DishEntity.DataEntity> initialCallback, final LoadCallback<Integer, DishEntity.DataEntity> afterCallback) {
        MyApplication.getInstance()
                .getRetrofit()
                .create(DishService.class)
                .getDish(limit, page)
                .map(new Function<DishEntity, List<DishEntity.DataEntity>>() {
                    @Override
                    public List<DishEntity.DataEntity> apply(DishEntity dishEntity) {
                        return dishEntity.getData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DishEntity.DataEntity>>() {
                               @Override
                               public void accept(List<DishEntity.DataEntity> list) {
                                   if (initialCallback != null) {
                                       initialCallback.onResult(list, null, 2);
                                       mIsRefreshing.setValue(false);
                                   } else {
                                       afterCallback.onResult(list, page + 1);
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                if (initialCallback != null) {
                                    mIsRefreshing.setValue(false);
                                }
                            }
                        });
    }

    public static class Factory extends DataSource.Factory<Integer, DishEntity.DataEntity> {

        private MutableLiveData<DishDataSource> mDataSource = new MutableLiveData<>();

        @Override
        public DataSource<Integer, DishEntity.DataEntity> create() {
            DishDataSource dataSource = new DishDataSource();
            mDataSource.postValue(dataSource);
            return dataSource;
        }

        public MutableLiveData<DishDataSource> getDataSource() {
            return mDataSource;
        }
    }


}
