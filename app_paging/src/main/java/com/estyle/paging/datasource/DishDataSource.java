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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DishDataSource extends PageKeyedDataSource<Integer, DishEntity.DataEntity> {

    private static final String TAG = DishDataSource.class.getSimpleName();

    // SwipeRefreshLayout刷新状态
    private MutableLiveData<Boolean> mIsRefreshing = new MutableLiveData<>();

    public MutableLiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    public MutableLiveData<Runnable> mRetry = new MutableLiveData<>();

    public MutableLiveData<String> mErrorMsg = new MutableLiveData<>();

    /**
     * 加载初始页
     *
     * @param params   加载初始页的参数，包括初始页item数量等信息
     * @param callback 加载初始页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadInitial, page size: " + params.requestedLoadSize);
        loadData(params, null, callback, null);
    }

    /**
     * 加载上一页
     *
     * @param params   加载上一页的参数，包括上一页item数量、上一页的关键词key等信息
     * @param callback 加载上一页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadBefore, thread: " + Thread.currentThread().getName() + ", size: " + params.requestedLoadSize + ", key: " + params.key);
    }

    /**
     * 加载下一页
     *
     * @param params   加载下一页的参数，包括下一页item数量、下一页的关键词key等信息
     * @param callback 加载下一页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull LoadCallback<Integer, DishEntity.DataEntity> callback) {
        ZYLog.e(TAG, "loadAfter,  size: " + params.requestedLoadSize + ", key: " + params.key);
        loadData(null, params, null, callback);
    }

    /**
     * 加载网络数据
     * <p>
     * //     * @param initialCallback 初始页使用的callback，没有则传null
     * //     * @param afterCallback   分页使用的callback，没有则传null
     */
    private void loadData(final LoadInitialParams<Integer> initialParams, final LoadParams<Integer> afterParams, final LoadInitialCallback<Integer, DishEntity.DataEntity> initialCallback, final LoadCallback<Integer, DishEntity.DataEntity> afterCallback) {
        MyApplication.getInstance()
                .getRetrofit()
                .create(DishService.class)
                .getDish(
                        initialParams != null ? initialParams.requestedLoadSize : afterParams.requestedLoadSize,
                        initialParams != null ? 1 : afterParams.key)// 分页item数量，分页页码
                .enqueue(new Callback<DishEntity>() {
                    @Override
                    public void onResponse(@NonNull Call<DishEntity> call, @NonNull Response<DishEntity> response) {
                        List<DishEntity.DataEntity> list = response.body().getData();
                        ZYLog.e("dish datasource ,tag: after", "list : " + list);

                        if (initialCallback != null) {
                            // 初始页数据加载完毕后，通过回调将数据传至容器
                            // 如果没有上一页，则传null
                            // 设置初始页之后的分页固定为第二页
                            initialCallback.onResult(list, null, 2);
                            mIsRefreshing.setValue(false);
                        } else {
                            // 分页数据加载完毕后，通过回调将数据传至容器
                            // 设置之后的分页固定为第N+1页
                            afterCallback.onResult(list, afterParams.key + 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<DishEntity> call, Throwable t) {
                        ZYLog.e("data source failure", t.getMessage());
                        if (initialCallback != null) {
                            mIsRefreshing.setValue(false);
                            mRetry.setValue(new Runnable() {
                                @Override
                                public void run() {
                                    loadInitial(initialParams, initialCallback);
                                }
                            });
                        } else {
                            mRetry.setValue(new Runnable() {
                                @Override
                                public void run() {
                                    loadAfter(afterParams, afterCallback);
                                }
                            });
                        }

                        mErrorMsg.setValue("数据加载失败，请重试");
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
