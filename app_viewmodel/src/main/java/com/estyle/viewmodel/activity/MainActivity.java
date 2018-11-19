package com.estyle.viewmodel.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.estyle.viewmodel.R;
import com.estyle.viewmodel.adapter.MainPagerAdapter;
import com.estyle.viewmodel.fragment.MainFragment;
import com.estyle.viewmodel.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * View层
 */
public class MainActivity extends AppCompatActivity implements Observer<List<MainViewModel.MainTab>> {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MainPagerAdapter mPagerAdapter;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getTabList().observe(this, this);

        initView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewModel.initTabs();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onChanged(@Nullable List<MainViewModel.MainTab> tabList) {
        List<MainFragment> fragmentList = new ArrayList<>();
        for (MainViewModel.MainTab tab : tabList) {
            fragmentList.add(MainFragment.newInstance(tab.getCode()));
        }
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
    }
}
