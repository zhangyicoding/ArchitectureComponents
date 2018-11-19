package com.estyle.databinding.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.databinding.entity.MainEntity;
import com.estyle.databinding.entity.MainListEntity;
import com.estyle.databinding.R;
import com.estyle.databinding.adapter.MainAdapter;
import com.estyle.databinding.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyi on 2018/10/8.
 */
public class MainFragment extends Fragment {

    private List<MainListEntity> mDatas;
    private FragmentMainBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
                .getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DataBindingUtil.getBinding(view);

        mDatas = new ArrayList<>();
        mBinding.recyclerView.setAdapter(new MainAdapter(mDatas));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 模拟数据
        MainEntity mainEntity = new MainEntity();
        List<MainListEntity> data = new ArrayList<>();
        mainEntity.setData(data);
        for (int i = 0; i < 20; i++) {
            MainListEntity entity = new MainListEntity();
            entity.setUsername("用户" + i);
            entity.setAvatar("https://www.baidu.com/img/bd_logo1.png");
            entity.setVip(false);
            data.add(entity);
        }

        mBinding.setEntity(mainEntity);
    }
}
