package com.estyle.app_room.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.estyle.app_room.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected AppCompatButton roomBtn;
    protected AppCompatButton roomRxjavaBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.room_btn) {
            intent.setClass(this, RoomActivity.class);
        } else if (view.getId() == R.id.room_rxjava_btn) {
            intent.setClass(this, RxRoomActivity.class);
        }
        startActivity(intent);
    }

    private void initView() {
        roomBtn = (AppCompatButton) findViewById(R.id.room_btn);
        roomBtn.setOnClickListener(MainActivity.this);
        roomRxjavaBtn = (AppCompatButton) findViewById(R.id.room_rxjava_btn);
        roomRxjavaBtn.setOnClickListener(MainActivity.this);
    }
}
