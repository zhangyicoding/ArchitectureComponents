package com.estyle.databinding.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;

import com.estyle.databinding.BR;


/**
 * Created by zhangyi on 2018/10/8.
 */
public class MainListEntity extends BaseObservable {

    private ObservableField<String> username = new ObservableField<>();
    private String avatar;
    private boolean isVip;

    @Bindable
    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
        notifyPropertyChanged(BR.username);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public String toString() {
        return "MainListEntity{" +
                "username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isVip=" + isVip +
                '}';
    }
}
