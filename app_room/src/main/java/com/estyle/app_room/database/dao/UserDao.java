package com.estyle.app_room.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.estyle.app_room.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    String TABLE_NAME = "user";

    @Insert()
    long insertUser(UserEntity users);

    @Delete
    int deleteUser(UserEntity... users);

    @Update
    int updateUser(UserEntity... users);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE account = :account")
    UserEntity queryUserByAccount(String account);

    @Query("SELECT * FROM " + TABLE_NAME)
    List<UserEntity> queryUsers();

    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<UserEntity>> queryLiveDataUsers();
}
