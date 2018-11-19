package com.estyle.app_room;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.estyle.app_room.database.AppDatabase;

/**
 * Created by zhangyi on 2018/11/9.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;
    private AppDatabase mDatabase;

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabase = initDatabase();
    }

    public AppDatabase initDatabase() {
        return mDatabase = Room.databaseBuilder(this, AppDatabase.class, "room.db")
//                .addMigrations()// 数据库版本迁移
                .build();
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }
}
