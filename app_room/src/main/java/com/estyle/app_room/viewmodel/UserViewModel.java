package com.estyle.app_room.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.estyle.app_room.MyApplication;
import com.estyle.app_room.database.dao.UserDao;
import com.estyle.app_room.entity.UserEntity;
import com.estyle.app_room.zhangyi.ZYLog;

import java.util.List;
import java.util.concurrent.Executors;

public class UserViewModel extends ViewModel {

    private MutableLiveData<List<UserEntity>> mList = new MutableLiveData<>();

    public MutableLiveData<List<UserEntity>> getList() {
        return mList;
    }

    private UserDao mUserDao;

    public UserViewModel() {
        mUserDao = MyApplication.getInstance()
                .getDatabase()
                .userDao();
    }

    public void saveUser(final String account, final String password) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                UserEntity user = new UserEntity();
                user.setAccount(account);
                user.setPassword(password);
                try {
                    long id = mUserDao.insertUser(user);
                    ZYLog.e("insert id", id);
                } catch (Exception e) {
                    ZYLog.e("insert exception", e.getMessage());
                }
            }
        });
    }


    public void deleteUser(final String account) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                UserEntity user = mUserDao.queryUserByAccount(account);
                try {
                    int count = mUserDao.deleteUser(user);
                    ZYLog.e("delete count: " + count);
                } catch (Exception e) {
                    ZYLog.e("delete exception", e.getMessage());
                }
            }
        });
    }

    public void updatePassword(final String account, final String newPassowrd) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                UserEntity user = mUserDao.queryUserByAccount(account);
                try {
                    user.setPassword(newPassowrd);
                    int count = mUserDao.updateUser(user);
                    ZYLog.e("update count: " + count);
                } catch (Exception e) {
                    ZYLog.e("update exception", e.getMessage());
                }
            }
        });
    }

    public void showUser(String account) {
    }

    public void showUsers() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<UserEntity> users = mUserDao.queryUsers();
                mList.postValue(users);
            }
        });
    }

}
