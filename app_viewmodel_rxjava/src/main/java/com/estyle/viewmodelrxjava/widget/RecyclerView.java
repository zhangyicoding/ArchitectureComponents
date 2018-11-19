package com.estyle.viewmodelrxjava.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class RecyclerView extends android.support.v7.widget.RecyclerView {

    private boolean mIsScrollToBottom;
    public boolean mIsLoading;

    private LinearLayoutManager mLayoutManager;
    private OnLoadMoreListener mOnLoadMoreListener;

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLayoutManager = new LinearLayoutManager(context);
        setLayoutManager(mLayoutManager);
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == SCROLL_STATE_IDLE && mIsScrollToBottom && !mIsLoading) {
                mIsLoading = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }

        @Override
        public void onScrolled(android.support.v7.widget.RecyclerView recyclerView,
                               int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
            int itemCount = mLayoutManager.getItemCount();
            mIsScrollToBottom = lastVisibleItemPosition == itemCount - 1;
        }
    };

    public void loadFinish() {
        mIsLoading = false;
    }

    public void removeOnScrollListener() {
        removeOnScrollListener(onScrollListener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
        if (onLoadMoreListener != null) {
            addOnScrollListener(onScrollListener);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}