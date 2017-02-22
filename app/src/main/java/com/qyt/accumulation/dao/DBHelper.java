package com.qyt.accumulation.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangxina on 2017/2/22.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String SQL_CREATE_GOAL =
            "CREATE TABLE goal (" +
                    "id integer primary key autoincrement," +
                    "updateTime text," +
                    "name text, time integer);";

    private static final String SQL_CREATE_RECORD =
            "CREATE TABLE record (" +
                    "id integer primary key autoincrement," +
                    "createTime text, " +
                    "endTime text, " +
                    "updateTime text, " +
                    "time integer, " +
                    "name text, " +
                    "note text, " +
                    "star integer, " +
                    "startTime text, " +
                    "goalId integer);";

    private static final String SQL_CREATE_USER =
            "CREATE TABLE user (" +
                    "id integer primary key autoincrement," +
                    "email text, " +
                    "mobile text," +
                    "name text, " +
                    "password text, " +
                    "photo text);";

    private static DBHelper helper = null;

    public static DBHelper getInstance(Context ctx) {
        if (helper == null) {
            helper = new DBHelper(ctx);
        }
        return helper;
    }

    private DBHelper(Context context) {
        super(context, "accumulation.db", null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_GOAL);
        db.execSQL(SQL_CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS goal");
        db.execSQL("DROP TABLE IF EXISTS record");
        onCreate(db);
    }
}
