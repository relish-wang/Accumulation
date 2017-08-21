package wang.relish.accumulation.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by wangxina on 2017/2/22.
 */

class DBHelper private constructor(context: Context) : SQLiteOpenHelper(context, "accumulation.db", null, DBHelper.VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USER)
        db.execSQL(SQL_CREATE_GOAL)
        db.execSQL(SQL_CREATE_RECORD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS user")
        db.execSQL("DROP TABLE IF EXISTS goal")
        db.execSQL("DROP TABLE IF EXISTS record")
        onCreate(db)
    }

    companion object {

        private val VERSION = 6

        private val SQL_CREATE_GOAL =
                "CREATE TABLE goal (" +
                        "id integer primary key autoincrement," +
                        "mobile varchar(15) not null," +
                        "updateTime text," +
                        "name text, time integer," +
                        "Companion text);"

        private val SQL_CREATE_RECORD =
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
                        "goalId integer not null," +
                        "Companion text);"

        private val SQL_CREATE_USER =
                "CREATE TABLE user (" +
                        "id integer primary key autoincrement," +
                        "mobile varchar(15)," +
                        "name text, " +
                        "password text, " +
                        "photo text," +
                        "Companion text);"

        private var helper: DBHelper? = null

        fun getInstance(ctx: Context): DBHelper {
            if (helper == null) {
                helper = DBHelper(ctx)
            }
            return helper as DBHelper
        }
    }
}
