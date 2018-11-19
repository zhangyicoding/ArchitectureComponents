package com.estyle.viewmodel.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.estyle.viewmodel.fragment.MainFragment;
import com.estyle.viewmodel.viewmodel.MainViewModel;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<MainViewModel.MainTab> mTabList;
    private List<MainFragment> mFragmentList;

    public MainPagerAdapter(FragmentManager fm, List<MainFragment> fragmentList, List<MainViewModel.MainTab> tabList) {
        super(fm);
        mFragmentList = fragmentList;
        mTabList = tabList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabList.get(position).getTitle();
    }
}
