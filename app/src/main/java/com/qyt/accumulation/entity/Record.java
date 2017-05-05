package com.qyt.accumulation.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qyt.accumulation.App;
import com.qyt.accumulation.dao.BaseData;
import com.qyt.accumulation.dao.DBHelper;
import com.qyt.accumulation.util.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录
 * Created by Relish on 2016/11/10.
 */
public class Record extends BaseData {

    private long goalId;
    private String name;
    private Integer star;
    private String note;
    private Long time;//努力时间
    private String createTime;
    private String startTime;
    private String endTime;
    private String updateTime;

    public Record() {
        id = findMaxId()+1;
        goalId = -1;//未分类
        name = "[未命名]";
        star = 1;
        note = "";
        time = 0L;
        createTime = TimeUtil.getNowTime();
        startTime = createTime;
        updateTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTime() {
        if (time <= 0) {
            long et = TimeUtil.dateTimeToLong(endTime);
            long st = TimeUtil.dateTimeToLong(startTime);
            return et - st;
        }
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    @SuppressWarnings("ConstantConditions")
    public static List<Record> findRecordsByGoalId(long goalId) {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from record where goalId = ?", new String[]{goalId + ""});
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<Record> records = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getLong(cursor.getColumnIndex("id")));
                record.setGoalId(cursor.getLong(cursor.getColumnIndex("goalId")));
                record.setName(cursor.getString(cursor.getColumnIndex("name")));
                record.setStar(cursor.getInt(cursor.getColumnIndex("star")));
                record.setNote(cursor.getString(cursor.getColumnIndex("note")));
                record.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                record.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                record.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                record.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
                record.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    public static long findMaxId() {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(id) from record", new String[]{});
        int max = 0;
        if (cursor != null && cursor.moveToFirst()) {
            max = cursor.getInt(0);
            cursor.close();
            db.close();
        }
        return max;
    }

    public static void delete(Record record) {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete("record", "id = ?", new String[]{record.getId() + ""});
        db.close();
    }

    public Goal getParent() {
        return Goal.findById(goalId);
    }

    public String getHardTime() {
        return TimeUtil.getHardTime(getTime());
    }

    @Override
    public long save() {
        return super.save();
    }

    public static long findMaxIdWhereGoalIdIs(int goalId) {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(id) from record where goalId = ?", new String[]{goalId + ""});
        int max = 0;
        if (cursor != null && cursor.moveToFirst()) {
            max = cursor.getInt(0);
            cursor.close();
            db.close();
        }
        return max;
    }

    public static List<Record> findAllUntitled() {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from record where goalId = ?", new String[]{"0"});
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<Record> records = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getLong(cursor.getColumnIndex("id")));
                record.setGoalId(cursor.getLong(cursor.getColumnIndex("goalId")));
                record.setName(cursor.getString(cursor.getColumnIndex("name")));
                record.setStar(cursor.getInt(cursor.getColumnIndex("star")));
                record.setNote(cursor.getString(cursor.getColumnIndex("note")));
                record.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                record.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                record.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                record.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
                record.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }
}