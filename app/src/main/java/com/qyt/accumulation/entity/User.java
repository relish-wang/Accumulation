package com.qyt.accumulation.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.qyt.accumulation.App;
import com.qyt.accumulation.dao.BaseData;
import com.qyt.accumulation.dao.DBHelper;

import java.io.Serializable;

/**
 * 用户实体类
 * Created by Relish on 2016/12/3.
 */
public class User extends BaseData implements Serializable {

    private String name;
    private String password;
    private String mobile;
    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(password) &&
                TextUtils.isEmpty(mobile) &&
                TextUtils.isEmpty(photo);
    }

    public static User login(String account, String password) {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User where mobile = ? and password = ?",
                new String[]{account, password});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            user.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
            db.close();
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return user;
    }

    public static User findByMobile(String mobile) {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user where mobile = ?", new String[]{mobile});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            user.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
            db.close();
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return user;
    }
}
