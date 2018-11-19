package com.estyle.viewmodel.repository;


import com.estyle.viewmodel.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Model层
 * 向Presenter层提供数据
 * 这是统一的数据提供层，但数据来源不同，该层会直接联系到不同的数据源，
 * 例如：网络数据联系到WebDatasource，DB数据联系到DBDatasource，内存数据直接产生，文件、SP等联系到相关数据源
 */
public class MainRepository {

    public List<MainViewModel.MainTab> getTabs() {
        List<MainViewModel.MainTab> tabList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            MainViewModel.MainTab tab = new MainViewModel.MainTab();
            tab.setTitle("标题" + i);
            tab.setCode(i);
            tabList.add(tab);
            tab = null;
        }
        return tabList;
    }
}
