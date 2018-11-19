package com.estyle.app_room.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.estyle.app_room.entity.UserEntity;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * RxJava2
 * Single 只发射一条数据，或一个异常通知，不发射完成通知
 * Completable 只发射一个完成通知，或一个异常，不发射数据
 * Maybe 只发射一条数据 + 一个完成通知，或，只发射一条数据 + 一个异常通知
 */
@Dao
public interface RxUserDao {

    String TABLE_NAME = "user";

    @Insert()
    long insertUser(UserEntity users);

    @Delete
    int deleteUser(UserEntity... users);

    @Query("DELETE FROM " + TABLE_NAME + " WHERE account = :account")
    int deleteUserByAccount(String account);

    @Update
    int updateUser(UserEntity... users);

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<UserEntity>> queryUsers();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE account = :account")
    Flowable<UserEntity> queryUserByAccount(String account);
}
