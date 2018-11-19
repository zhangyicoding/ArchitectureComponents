package com.estyle.paging.adapter;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estyle.paging.R;
import com.estyle.paging.entity.DishEntity;

public class DishAdapter extends PagedListAdapter<DishEntity.DataEntity, DishAdapter.ViewHolder> {

    public DishAdapter() {
        super(sCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish, parent, false));
            mTextView = itemView.findViewById(R.id.title_tv);
        }

        private void bind(DishEntity.DataEntity dish) {
            mTextView.setText(dish.getTitle());
        }
    }

    // RecyclerView附属的比较新旧item差异的回调
    private static DiffUtil.ItemCallback<DishEntity.DataEntity> sCallback = new DiffUtil.ItemCallback<DishEntity.DataEntity>() {

        // 比较新旧item对象是否相同
        @Override
        public boolean areItemsTheSame(@NonNull DishEntity.DataEntity oldItem, @NonNull DishEntity.DataEntity newItem) {
            return oldItem == newItem;
        }

        // 比较新旧item内容是否相同
        @Override
        public boolean areContentsTheSame(@NonNull DishEntity.DataEntity oldItem, @NonNull DishEntity.DataEntity newItem) {
            return TextUtils.equals(oldItem.getTitle(), newItem.getTitle());
        }
    };
}