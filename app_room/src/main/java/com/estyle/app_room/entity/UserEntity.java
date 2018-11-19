package com.estyle.app_room.entity;

import android.arch.persistence.room.*;

/**
 * Created by zhangyi on 2018/11/9.
 */
@Entity(tableName = "user", indices = {@Index(value = {"account"}, unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long primaryID;

    private String account;
    private String password;

//    @ColumnInfo(name = "register_time")
//    private Date registerTime;

    @Ignore
    private int ignore;


    public long getPrimaryID() {
        return primaryID;
    }

    public void setPrimaryID(long primaryID) {
        this.primaryID = primaryID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "primaryID=" + primaryID +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
//                ", registerTime=" + registerTime +
                '}';
    }
}