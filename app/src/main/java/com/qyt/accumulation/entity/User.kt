package com.qyt.accumulation.entity

import android.text.TextUtils
import com.qyt.accumulation.App
import com.qyt.accumulation.dao.BaseData
import com.qyt.accumulation.dao.DBHelper

/**
 * 用户实体类
 * Created by Relish on 2016/12/3.
 */
class User : BaseData() {

    var name: String? = null
    var password: String? = null
    var mobile: String? = null
    var photo: String? = null

    val isEmpty: Boolean
        get() = TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(password) &&
                TextUtils.isEmpty(mobile) &&
                TextUtils.isEmpty(photo)

    companion object {

        fun login(account: String, password: String): User? {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from User where mobile = ? and password = ?",
                    arrayOf(account, password))
            var user: User? = null
            if (cursor != null && cursor.moveToFirst()) {
                user = User()
                user.name = cursor.getString(cursor.getColumnIndex("name"))
                user.password = cursor.getString(cursor.getColumnIndex("password"))
                user.mobile = cursor.getString(cursor.getColumnIndex("mobile"))
                user.photo = cursor.getString(cursor.getColumnIndex("photo"))
                db.close()
                cursor.close()
            }
            if (db.isOpen) {
                db.close()
            }
            return user
        }

        fun findByMobile(mobile: String): User? {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from user where mobile = ?", arrayOf(mobile))
            var user: User? = null
            if (cursor != null && cursor.moveToFirst()) {
                user = User()
                user.name = cursor.getString(cursor.getColumnIndex("name"))
                user.password = cursor.getString(cursor.getColumnIndex("password"))
                user.mobile = cursor.getString(cursor.getColumnIndex("mobile"))
                user.photo = cursor.getString(cursor.getColumnIndex("photo"))
                db.close()
                cursor.close()
            }
            if (db.isOpen) {
                db.close()
            }
            return user
        }
    }
}
