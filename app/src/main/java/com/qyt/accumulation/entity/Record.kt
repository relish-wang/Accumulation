package com.qyt.accumulation.entity

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import com.qyt.accumulation.App
import com.qyt.accumulation.dao.BaseData
import com.qyt.accumulation.dao.DBHelper
import com.qyt.accumulation.util.TimeUtil

import java.util.ArrayList

/**
 * 记录
 * Created by Relish on 2016/11/10.
 */
class Record : BaseData() {

    var goalId: Long = 0
    var name: String? = null
    var star: Int? = null
    var note: String? = null
    var time: Long? = null
        get() {
            if (field!! <= 0) {
                val et = TimeUtil.dateTimeToLong(endTime!!)
                val st = TimeUtil.dateTimeToLong(startTime!!)
                return et - st
            }
            return field
        }//努力时间
    var createTime: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var updateTime: String? = null

    init {
        id = findMaxId() + 1
        goalId = -1//未分类
        name = "[未命名]"
        star = 1
        note = ""
        this.time = 0L
        createTime = TimeUtil.nowTime
        startTime = createTime
        updateTime = createTime
    }

    val parent: Goal
        get() = Goal.findById(goalId)

    val hardTime: String
        get() = TimeUtil.getHardTime(time!!)

    override fun save(): Long {
        return super.save()
    }

    companion object {


        fun findRecordsByGoalId(goalId: Long): List<Record> {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from record where goalId = ?", arrayOf(goalId.toString() + "")) ?: return ArrayList()
            val records = ArrayList<Record>()
            if (cursor.moveToFirst()) {
                do {
                    val record = Record()
                    record.id = cursor.getLong(cursor.getColumnIndex("id"))
                    record.goalId = cursor.getLong(cursor.getColumnIndex("goalId"))
                    record.name = cursor.getString(cursor.getColumnIndex("name"))
                    record.star = cursor.getInt(cursor.getColumnIndex("star"))
                    record.note = cursor.getString(cursor.getColumnIndex("note"))
                    record.time = cursor.getLong(cursor.getColumnIndex("time"))
                    record.updateTime = cursor.getString(cursor.getColumnIndex("updateTime"))
                    record.createTime = cursor.getString(cursor.getColumnIndex("createTime"))
                    record.startTime = cursor.getString(cursor.getColumnIndex("startTime"))
                    record.endTime = cursor.getString(cursor.getColumnIndex("endTime"))
                    records.add(record)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return records
        }

        fun findMaxId(): Long {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select max(id) from record", arrayOf<String>())
            var max = 0
            if (cursor != null && cursor.moveToFirst()) {
                max = cursor.getInt(0)
                cursor.close()
                db.close()
            }
            return max.toLong()
        }

        fun delete(record: Record) {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            db.delete("record", "id = ?", arrayOf(record.id.toString() + ""))
            db.close()
        }

        fun findMaxIdWhereGoalIdIs(goalId: Int): Long {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select max(id) from record where goalId = ?", arrayOf(goalId.toString() + ""))
            var max = 0
            if (cursor != null && cursor.moveToFirst()) {
                max = cursor.getInt(0)
                cursor.close()
                db.close()
            }
            return max.toLong()
        }

        fun findAllUntitled(): List<Record> {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from record where goalId = ?", arrayOf("0")) ?: return ArrayList()
            val records = ArrayList<Record>()
            if (cursor.moveToFirst()) {
                do {
                    val record = Record()
                    record.id = cursor.getLong(cursor.getColumnIndex("id"))
                    record.goalId = cursor.getLong(cursor.getColumnIndex("goalId"))
                    record.name = cursor.getString(cursor.getColumnIndex("name"))
                    record.star = cursor.getInt(cursor.getColumnIndex("star"))
                    record.note = cursor.getString(cursor.getColumnIndex("note"))
                    record.time = cursor.getLong(cursor.getColumnIndex("time"))
                    record.updateTime = cursor.getString(cursor.getColumnIndex("updateTime"))
                    record.createTime = cursor.getString(cursor.getColumnIndex("createTime"))
                    record.startTime = cursor.getString(cursor.getColumnIndex("startTime"))
                    record.endTime = cursor.getString(cursor.getColumnIndex("endTime"))
                    records.add(record)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return records
        }

        /**
         * 根据recordId删除Record
         * @param id recordI
         * *
         * @return the number of rows affected if a whereClause is passed in, 0
         * *         otherwise. To remove all rows and get a count pass "1" as the
         * *         whereClause.
         */
        fun remove(id: Long): Int {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            return db.delete("record", "id = ?", arrayOf(id.toString() + ""))
        }
    }
}