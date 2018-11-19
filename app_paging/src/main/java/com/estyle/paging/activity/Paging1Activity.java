package com.estyle.paging.activity;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.estyle.paging.R;
import com.estyle.paging.adapter.DishAdapter;
import com.estyle.paging.datasource.DishDataSource;
import com.estyle.paging.entity.DishEntity;

/**
 * MVC架构的Paging
 */
public class Paging1Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSnackbar;

    private LiveData<PagedList<DishEntity.DataEntity>> mList;

    private DishDataSource.Factory mDataSourceFactory;
    private DishAdapter mDishAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);
        setTitle("Paging MVC");

        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mDishAdapter = new DishAdapter();
        mRecyclerView.setAdapter(mDishAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataSourceFactory.getDataSource().getValue().invalidate();
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }
        });

        mDataSourceFactory = new DishDataSource.Factory();

        LiveData<Boolean> isRefreshing = Transformations.switchMap(mDataSourceFactory.getDataSource(), new Function<DishDataSource, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(DishDataSource dataSource) {
                return dataSource.isRefreshing();
            }
        });

        isRefreshing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isRefreshing) {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

        LiveData<String> errorMsg = Transformations.switchMap(mDataSourceFactory.getDataSource(), new Function<DishDataSource, LiveData<String>>() {
            @Override
            public LiveData<String> apply(DishDataSource dataSource) {
                return dataSource.mErrorMsg;
            }
        });

        errorMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMsg) {
                mSnackbar = Snackbar.make(mSwipeRefreshLayout, errorMsg, Snackbar.LENGTH_INDEFINITE)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDataSourceFactory.getDataSource().getValue().mRetry.getValue().run();
                            }
                        });
                mSnackbar.show();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                mList = new LivePagedListBuilder<>(
                        mDataSourceFactory,
                        new PagedList.Config.Builder()
                                .setInitialLoadSizeHint(10)
                                .setPageSize(10)
                                .setPrefetchDistance(2)
                                .build()
                ).build();

                mList.observe(Paging1Activity.this, new Observer<PagedList<DishEntity.DataEntity>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<DishEntity.DataEntity> list) {
                        mDishAdapter.submitList(list);
                    }
                });
            }
        });
    }
}
