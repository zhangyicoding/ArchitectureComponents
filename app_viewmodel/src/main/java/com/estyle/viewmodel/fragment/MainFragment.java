package com.estyle.viewmodel.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estyle.viewmodel.R;
import com.estyle.viewmodel.adapter.MainAdapter;
import com.estyle.viewmodel.viewmodel.MainListViewModel;
import com.estyle.viewmodel.widget.RecyclerView;

/**
 * View层
 */
public class MainFragment extends Fragment
        implements Observer<MainListViewModel.MainListBean>, SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnLoadMoreListener {

    private int mCode;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTipView;
    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;

    private MainListViewModel mViewModel;

    public static MainFragment newInstance(int code) {
        Bundle args = new Bundle();
        args.putInt("code", code);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCode = getArguments().getInt("code");

        mViewModel = ViewModelProviders.of(this).get(MainListViewModel.class);
        mViewModel.getLiveData().observe(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mTipView = view.findViewById(R.id.tip_view);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setOnLoadMoreListener(this);

        mAdapter = new MainAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshData();
    }

    @Override
    public void onLoadMore() {
        mViewModel.loadMore();
    }

    @Override
    public void onChanged(@Nullable MainListViewModel.MainListBean mainListBean) {
        if (mainListBean.isShowTip()) {
            mTipView.setVisibility(View.VISIBLE);
            mTipView.setText(mainListBean.getTip());
        } else {
            mTipView.setVisibility(View.GONE);
            mAdapter.setDatas(mainListBean.getDatas());
            if (mainListBean.isRefresh()) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mRecyclerView.loadFinish();
            }
        }
    }
}
