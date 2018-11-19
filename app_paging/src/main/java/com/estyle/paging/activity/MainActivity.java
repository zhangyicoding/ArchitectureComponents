package com.estyle.paging.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.estyle.paging.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button btn1;
    protected Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.btn1) {
            intent.setClass(this, Paging1Activity.class);
        } else if (view.getId() == R.id.btn2) {
            intent.setClass(this, Paging2Activity.class);
        }
        startActivity(intent);
    }

    private void initView() {
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(MainActivity.this);
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(MainActivity.this);
    }
}
