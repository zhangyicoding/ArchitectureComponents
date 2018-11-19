package com.estyle.databinding.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.estyle.databinding.R;
import com.estyle.databinding.databinding.ActivityMainBinding;

/**
 * Created by zhangyi on 2018/10/8.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }
}
