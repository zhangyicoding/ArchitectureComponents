package com.estyle.databinding.databinding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.estyle.databinding.adapter.MainAdapter;
import com.estyle.databinding.entity.MainListEntity;

import java.util.List;

/**
 * Created by zhangyi on 2018/10/8.
 * 自定义属性，实现接收属性值并处理
 */
public class MyBindingAdapter {

    // 使用@BindingAdapter修饰的方法会被DataBinding库扫描到，必须public static修饰
    @BindingAdapter("datas")// 一个属性
    public static void setListData(RecyclerView recyclerView, List<MainListEntity> datas) {
        ((MainAdapter) recyclerView.getAdapter()).addDatas(datas);
    }


    @BindingAdapter(value = {"url", "circle"}, requireAll = false)// 多个属性，且指定是否全部必须
    public static void setImage(ImageView imageView, String url, boolean isCircle) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(imageView.getContext())
                .load(url);

        if (isCircle) {
            requestBuilder.apply(RequestOptions.circleCropTransform());
        }

        requestBuilder.into(imageView);
    }
}
