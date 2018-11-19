package com.estyle.app_room.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.estyle.app_room.database.dao.RxUserDao;
import com.estyle.app_room.database.dao.UserDao;
import com.estyle.app_room.entity.UserEntity;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract RxUserDao rxUserDao();
}
