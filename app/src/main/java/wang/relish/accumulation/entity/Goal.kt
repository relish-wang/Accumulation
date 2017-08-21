package wang.relish.accumulation.entity

import wang.relish.accumulation.App
import wang.relish.accumulation.dao.BaseData
import wang.relish.accumulation.dao.DBHelper
import wang.relish.accumulation.util.AppLog
import wang.relish.accumulation.util.TimeUtil
import java.util.*

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
class Goal : BaseData() {

    var mobile: String? = null
    var name: String? = null
    private var time: Long = 0
    var updateTime: String? = null
    internal var records: List<Record>? = null

    init {
        records = ArrayList<Record>()
    }


    fun getRecord(recordId: Int): Record? {
        if (records == null || records!!.size == 0) {
            getRecords()
            if (records == null || records!!.size == 0) {
                AppLog.e("Goal", "getRecord(int)", "There is NO record in this goal.")
                return null
            } else {
                for (record in records!!) {
                    if (record.id == recordId.toLong()) {
                        return record
                    }
                }
            }
        }
        for (record in records!!) {
            if (record.id == recordId.toLong()) {
                return record
            }
        }
        AppLog.e("Goal", "getRecord(int)", "RecordID[$recordId] NOT found.")
        return null
    }

    fun setTime(time: Long) {
        this.time = time
    }

    fun getTime(): Long? {
        return time
    }

    fun setTime(time: Long?) {
        this.time = time!!
        this.updateTime = TimeUtil.longToDateTime(time)
    }

    fun getRecords(): List<Record>? {
        val helper = DBHelper.getInstance(App.CONTEXT!!)
        val db = helper.readableDatabase
        if (records == null || records!!.size == 0) {
            val rs = ArrayList<Record>()
            val cursor = db.rawQuery(
                    "select * from record where goalId = ?", arrayOf(super.id.toString() + ""))
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val r = Record()
                    r.id = cursor.getLong(cursor.getColumnIndex("id"))
                    r.goalId = super.id
                    r.name = cursor.getString(cursor.getColumnIndex("name"))
                    r.star = cursor.getInt(cursor.getColumnIndex("star"))
                    r.note = cursor.getString(cursor.getColumnIndex("note"))
                    r.time = cursor.getLong(cursor.getColumnIndex("time"))
                    r.createTime = cursor.getString(cursor.getColumnIndex("createtime"))
                    r.startTime = cursor.getString(cursor.getColumnIndex("starttime"))
                    r.endTime = cursor.getString(cursor.getColumnIndex("endtime"))
                    r.updateTime = cursor.getString(cursor.getColumnIndex("updatetime"))
                    rs.add(r)
                } while (cursor.moveToNext())
            }
            cursor!!.close()
            db.close()
            records = rs
            return rs
        }
        return records
    }

    fun setRecords(records: List<Record>?) {
        if (records == null || records.size == 0)
            this.records = ArrayList<Record>()
        this.records = records
    }

    val hardHour: Int
        get() {
            var time = hardTimeLong
            time = time?.div(1000)
            return (time!! / 3600).toInt()
        }

    val hardMinute: Int
        get() {
            var time = hardTimeLong
            time = time?.div(1000)
            return (time!! % 3600 / 60).toInt()
        }

    val hardTime: String
        get() {
            var time = hardTimeLong
            time = time?.div(1000)
            val hour = (time!! / 3600).toInt()
            val min = (time % 3600 / 60).toInt()
            val sec = (time % 60).toInt()
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, sec)
        }

    val hardTimeLong: Long?
        get() {
            var time: Long? = 0L
            for (record in records!!) {
                time = time?.plus(record.time!!)
            }
            return time
        }

    companion object {

        fun findAll(): List<Goal> {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from goal", arrayOf<String>()) ?: return ArrayList()
            val goals = ArrayList<Goal>()
            if (cursor.moveToFirst()) {
                do {
                    val goal = Goal()
                    goal.id = cursor.getLong(cursor.getColumnIndex("id"))
                    goal.mobile = cursor.getString(cursor.getColumnIndex("mobile"))
                    goal.name = cursor.getString(cursor.getColumnIndex("name"))
                    goal.setTime(cursor.getLong(cursor.getColumnIndex("time")))

                    val records = Record.findRecordsByGoalId(goal.id)
                    goal.setRecords(records ?: ArrayList<Record>())
                    goals.add(goal)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return goals
        }

        fun deleteItAndItsRecords(group: Goal) {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            db.execSQL("delete from record where goalId = ?", arrayOf(group.id.toString() + ""))
            db.execSQL("delete from goal where id = ?", arrayOf(group.id.toString() + ""))
        }

        val maxId: Long
            get() {
                val helper = DBHelper.getInstance(App.CONTEXT!!)
                val db = helper.readableDatabase
                val cursor = db.rawQuery("select max(id) from goal", arrayOf<String>())
                var max: Long = 1
                if (cursor != null && cursor.moveToFirst()) {
                    max = cursor.getLong(0)
                    cursor.close()
                    db.close()
                }
                return max
            }

        fun findById(id: Long): Goal? {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from Goal where id = ?", arrayOf(id.toString() + ""))
            var goal: Goal? = null
            if (cursor != null && cursor.moveToFirst()) {
                goal = Goal()
                goal.id = cursor.getLong(cursor.getColumnIndex("id"))
                goal.name = cursor.getString(cursor.getColumnIndex("name"))
                goal.mobile = cursor.getString(cursor.getColumnIndex("mobile"))
                goal.updateTime = cursor.getString(cursor.getColumnIndex("updateTime"))
                goal.setTime(cursor.getLong(cursor.getColumnIndex("time")))
                cursor.close()
                db.close()
            }
            return goal
        }
    }
}
