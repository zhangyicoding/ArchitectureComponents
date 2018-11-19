package com.estyle.paging.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.estyle.paging.entity.DishEntity;
import com.estyle.paging.viewmodel.DishViewModel;

/**
 * MVP架构的Paging
 */
public class Paging2Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSnackbar;

    private DishAdapter mDishAdapter;

    private DishViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);
        setTitle("Paging MVP");

        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mDishAdapter = new DishAdapter();
        mRecyclerView.setAdapter(mDishAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh();
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }
        });

        mViewModel = ViewModelProviders.of(this).get(DishViewModel.class);

        mViewModel.getIsRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isRefreshing) {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

        mViewModel.getErrorMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMsg) {
                mSnackbar = Snackbar.make(mSwipeRefreshLayout, errorMsg, Snackbar.LENGTH_INDEFINITE)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mViewModel.retry();
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

                mViewModel.refresh();
                mViewModel.getList().observe(Paging2Activity.this, new Observer<PagedList<DishEntity.DataEntity>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<DishEntity.DataEntity> list) {
                        mDishAdapter.submitList(list);
                    }
                });
            }
        });
    }
}
