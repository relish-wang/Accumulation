package cn.studyjams.s1.contest.accumulation.entity;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.studyjams.s1.contest.accumulation.util.AppLog;
import cn.studyjams.s1.contest.accumulation.util.TimeUtil;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class Goal extends DataSupport implements Serializable {

    private static final String TAG = "Goal";

    private long id;
    private String name;
    private long time;
    private List<Record> records;
    private String updateTime;

    public Goal() {
        records = new ArrayList<>();
    }

    public Record getRecord(int recordId) {
        if (records == null || records.size() == 0) {
            AppLog.e(TAG, "getRecord(int)", "There is NO record in this goal.");
            return null;
        }
        for (Record record : records) {
            if (record.getId() == recordId) {
                return record;
            }
        }
        AppLog.e(TAG, "getRecord(int)", "RecordID[" + recordId + "] NOT found.");
        return null;
    }


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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
        this.updateTime = TimeUtil.longToDateTime(time);
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<Record> getRecords() {
        if (records == null || records.size() == 0)
            return new ArrayList<>();
        return records;
    }

    public void setRecords(List<Record> records) {
        if (records == null || records.size() == 0)
            this.records = new ArrayList<>();
        this.records = records;
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
        Cursor cursor = DataSupport.findBySQL("select * from goal");
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<Goal> goals = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Goal goal = new Goal();
                goal.setId(cursor.getLong(cursor.getColumnIndex("id")));
                goal.setName(cursor.getString(cursor.getColumnIndex("name")));
                goal.setTime(cursor.getLong(cursor.getColumnIndex("time")));

                List<Record> records = Record.findRecordsByGoalId(goal.getId());
                goal.setRecords(records == null ? new ArrayList<>() : records);
                goals.add(goal);
            } while (cursor.moveToNext());
        }
        return goals;
    }

}
