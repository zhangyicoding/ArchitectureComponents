package com.estyle.app_room.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.estyle.app_room.R;
import com.estyle.app_room.entity.UserEntity;
import com.estyle.app_room.viewmodel.UserViewModel;
import com.estyle.app_room.zhangyi.ZYLog;

import java.util.List;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RoomActivity.class.getSimpleName();

    protected AppCompatEditText accountEt;
    protected AppCompatEditText passwordEt;
    protected AppCompatButton insertBtn;
    protected AppCompatButton deleteBtn;
    protected AppCompatButton updateBtn;
    protected AppCompatButton queryBtn;

    private UserViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initView();

        mViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel();
            }
        }).get(UserViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<UserEntity>>() {
            @Override
            public void onChanged(@Nullable List<UserEntity> list) {
                ZYLog.e();
                ZYLog.e(TAG, "size: " + list.size());
                for (UserEntity user : list) {
                    ZYLog.e(TAG, user);
                }
                ZYLog.e();
            }
        });

    }

    @Override
    public void onClick(View view) {
        String account = accountEt.getText().toString();
        String password = passwordEt.getText().toString();
        if (view.getId() == R.id.insert_btn) {
            mViewModel.saveUser(account, password);
        } else if (view.getId() == R.id.delete_btn) {
            mViewModel.deleteUser(account);
        } else if (view.getId() == R.id.update_btn) {
            mViewModel.updatePassword(account, password);
        } else if (view.getId() == R.id.query_btn) {
            mViewModel.showUsers();
        }
    }

    private void initView() {
        accountEt = (AppCompatEditText) findViewById(R.id.account_et);
        passwordEt = (AppCompatEditText) findViewById(R.id.password_et);
        insertBtn = (AppCompatButton) findViewById(R.id.insert_btn);
        insertBtn.setOnClickListener(this);
        deleteBtn = (AppCompatButton) findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);
        updateBtn = (AppCompatButton) findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(this);
        queryBtn = (AppCompatButton) findViewById(R.id.query_btn);
        queryBtn.setOnClickListener(this);
    }
}
