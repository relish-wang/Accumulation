package com.qyt.accumulation.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qyt.accumulation.App;
import com.qyt.accumulation.dao.BaseData;
import com.qyt.accumulation.dao.DBHelper;
import com.qyt.accumulation.util.AppLog;
import com.qyt.accumulation.util.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class Goal extends BaseData {

    private String mobile;
    private String name;
    private long time;
    private String updateTime;
    private List<Record> records;

    public Goal() {
        records = new ArrayList<>();
    }


    public Record getRecord(int recordId) {
        if (records == null || records.size() == 0) {
            getRecords();
            if (records == null || records.size() == 0) {
                AppLog.INSTANCE.e("Goal", "getRecord(int)", "There is NO record in this goal.");
                return null;
            } else {
                for (Record record : records) {
                    if (record.getId() == recordId) {
                        return record;
                    }
                }
            }
        }
        for (Record record : records) {
            if (record.getId() == recordId) {
                return record;
            }
        }
        AppLog.INSTANCE.e("Goal", "getRecord(int)", "RecordID[" + recordId + "] NOT found.");
        return null;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
        this.updateTime = TimeUtil.INSTANCE.longToDateTime(time);
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @SuppressWarnings("ConstantConditions")
    public List<Record> getRecords() {
        DBHelper helper = DBHelper.Companion.getInstance(App.Companion.getCONTEXT());
        SQLiteDatabase db = helper.getReadableDatabase();
        if (records == null || records.size() == 0) {
            List<Record> rs = new ArrayList<>();
            Cursor cursor = db.rawQuery(
                    "select * from record where goalId = ?", new String[]{super.getId() + ""});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Record r = new Record();
                    r.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    r.setGoalId(super.getId());
                    r.setName(cursor.getString(cursor.getColumnIndex("name")));
                    r.setStar(cursor.getInt(cursor.getColumnIndex("star")));
                    r.setNote(cursor.getString(cursor.getColumnIndex("note")));
                    r.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                    r.setCreateTime(cursor.getString(cursor.getColumnIndex("createtime")));
                    r.setStartTime(cursor.getString(cursor.getColumnIndex("starttime")));
                    r.setEndTime(cursor.getString(cursor.getColumnIndex("endtime")));
                    r.setUpdateTime(cursor.getString(cursor.getColumnIndex("updatetime")));
                    rs.add(r);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            records = rs;
            return rs;
        }
        return records;
    }

    public void setRecords(List<Record> records) {
        if (records == null || records.size() == 0)
            this.records = new ArrayList<>();
        this.records = records;
    }

    public int getHardHour() {
        Long time = getHardTimeLong();
        time /= 1000;
        return (int) (time / 3600);
    }

    public int getHardMinute() {
        Long time = getHardTimeLong();
        time /= 1000;
        return (int) (time % 3600 / 60);
    }

    public String getHardTime() {
        Long time = getHardTimeLong();
        time /= 1000;
        int hour = (int) (time / 3600);
        int min = (int) (time % 3600 / 60);
        int sec = (int) (time % 60);
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, sec);
    }

    public Long getHardTimeLong() {
        Long time = 0L;
        for (Record record : records) {
            time += record.getTime();
        }
        return time;
    }

    @SuppressWarnings("ConstantConditions")
    public static List<Goal> findAll() {
        DBHelper helper = DBHelper.Companion.getInstance(App.Companion.getCONTEXT());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from goal", new String[]{});
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<Goal> goals = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Goal goal = new Goal();
                goal.setId(cursor.getLong(cursor.getColumnIndex("id")));
                goal.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                goal.setName(cursor.getString(cursor.getColumnIndex("name")));
                goal.setTime(cursor.getLong(cursor.getColumnIndex("time")));

                List<Record> records = Record.Companion.findRecordsByGoalId(goal.getId());
                goal.setRecords(records == null ? new ArrayList<Record>() : records);
                goals.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return goals;
    }

    public static void deleteItAndItsRecords(Goal group) {
        DBHelper helper = DBHelper.Companion.getInstance(App.Companion.getCONTEXT());
        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL("delete from record where goalId = ?", new String[]{group.getId() + ""});
        db.execSQL("delete from goal where id = ?", new String[]{group.getId() + ""});
    }

    public static long getMaxId() {
        DBHelper helper = DBHelper.Companion.getInstance(App.Companion.getCONTEXT());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(id) from goal", new String[]{});
        long max = 1;
        if (cursor != null && cursor.moveToFirst()) {
            max = cursor.getLong(0);
            cursor.close();
            db.close();
        }
        return max;
    }

    public static Goal findById(long id) {
        DBHelper helper = DBHelper.Companion.getInstance(App.Companion.getCONTEXT());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Goal where id = ?", new String[]{id + ""});
        Goal goal = null;
        if (cursor != null && cursor.moveToFirst()) {
            goal = new Goal();
            goal.setId(cursor.getLong(cursor.getColumnIndex("id")));
            goal.setName(cursor.getString(cursor.getColumnIndex("name")));
            goal.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            goal.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
            goal.setTime(cursor.getLong(cursor.getColumnIndex("time")));
            cursor.close();
            db.close();
        }
        return goal;
    }
}
