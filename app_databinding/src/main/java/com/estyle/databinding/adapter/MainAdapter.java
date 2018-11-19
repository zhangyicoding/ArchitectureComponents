package com.estyle.databinding.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.estyle.databinding.R;
import com.estyle.databinding.databinding.ItemMainBinding;
import com.estyle.databinding.entity.MainListEntity;

import java.util.List;

/**
 * Created by zhangyi on 2018/10/8.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<MainListEntity> mDatas;

    public MainAdapter(List<MainListEntity> datas) {
        mDatas = datas;
    }

    public void addDatas(List<MainListEntity> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_main, parent, false)
                .getRoot();
        return new MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.bind(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        private ItemMainBinding mBinding;
        private MainListEntity mEntity;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.getBinding(itemView);
            mBinding.setHolder(this);
        }

        void bind(MainListEntity entity) {
            mEntity = entity;
            mBinding.setEntity(mEntity);
        }

        public void checkVIP(CompoundButton view, boolean isChecked) {
            mEntity.setUsername("是否是会员：" + isChecked);
        }
    }
}
