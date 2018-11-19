package com.estyle.viewmodel.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estyle.viewmodel.R;
import com.estyle.viewmodel.bean.DishBean;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<DishBean.DataBean> mDatas;

    public void setDatas(List<DishBean.DataBean> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<DishBean.DataBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mainlist, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.title_tv);
        }

        private void bind(DishBean.DataBean data) {
            mTitleTextView.setText(data.getTitle());
        }
    }
}
