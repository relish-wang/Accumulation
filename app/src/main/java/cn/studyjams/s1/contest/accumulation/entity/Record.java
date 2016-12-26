package cn.studyjams.s1.contest.accumulation.entity;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录
 * Created by Relish on 2016/11/10.
 */
public class Record extends DataSupport implements Serializable {

    private long id;
    private String name;
    private Integer star;
    private String note;
    private Long time;
    private String createTime;
    private String startTime;
    private String endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
        Cursor cursor = DataSupport.findBySQL("select * from record where goalId = ?", goalId + "");
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<Record> records = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getLong(cursor.getColumnIndex("id")));
                record.setName(cursor.getString(cursor.getColumnIndex("name")));
                record.setStar(cursor.getInt(cursor.getColumnIndex("star")));
                record.setNote(cursor.getString(cursor.getColumnIndex("note")));
                record.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                record.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                record.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
                record.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }
}