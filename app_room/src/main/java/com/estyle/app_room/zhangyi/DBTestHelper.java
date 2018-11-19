package com.estyle.app_room.zhangyi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * 辅助添加假数据
 */
public class DBTestHelper {

    public static final String TAG = DBTestHelper.class.getSimpleName();

    private DBHelper mDBHelper;

    public DBTestHelper(Context context) {
        mDBHelper = new DBHelper(context);
    }

    public void insertOnce() {
        insertFor(1);
    }

    public void insertFor(int count) {
        for (int i = 1; i <= count; i++) {
            String account = "admin" + i;
            String password = "123456";
            insert(account, password);
        }
    }

    public void insert(String account, String password) {
        ContentValues values = new ContentValues();
        values.put("account", account);
        values.put("password", password);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long insert = db.insert(DBHelper.TABLE_NAME, null, values);
        ZYLog.e(TAG, "insert: " + insert);
    }

    public void queryByAccount(String account) {
        Cursor cursor = mDBHelper.getWritableDatabase()
                .query(DBHelper.TABLE_NAME,
                        null,
                        "account = ?",
                        new String[]{account},
                        null, null, null);

        while (cursor.moveToNext()) {
            String account1 = cursor.getString(cursor.getColumnIndex("account"));
            ZYLog.e(TAG, "query account: " + account);
        }
    }

    public static class DBHelper extends SQLiteOpenHelper {

        public static final String TABLE_NAME = "table_user";

        public DBHelper(Context context) {
            super(context, "room.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS user(_id PRIMARY KEY AUTOINCREMENT, account, password)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
