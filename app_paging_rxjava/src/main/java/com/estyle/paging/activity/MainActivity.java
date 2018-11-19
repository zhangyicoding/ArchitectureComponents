package com.estyle.paging.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.estyle.paging.R;
import com.estyle.paging.adapter.DishAdapter;
import com.estyle.paging.entity.DishEntity;
import com.estyle.paging.viewmodel.DishViewModel;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * MVP + RxJava
 * 该demo只是演示了RxJava + Paging的用法，没有实现例如加载失败等细节
 * 详细参看LiveData + Paging
 */
public class MainActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private DishAdapter mDishAdapter;

    private DishViewModel mViewModel;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Paging MVP with Retrofit + RxJava");

        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mDishAdapter = new DishAdapter();
        mRecyclerView.setAdapter(mDishAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh();
            }
        });

        mViewModel = ViewModelProviders.of(this).get(DishViewModel.class);

        mViewModel.getIsRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isRefreshing) {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDisposable = mViewModel.getList().subscribe(new Consumer<PagedList<DishEntity.DataEntity>>() {
            @Override
            public void accept(PagedList<DishEntity.DataEntity> list) throws Exception {
                mDishAdapter.submitList(list);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}
