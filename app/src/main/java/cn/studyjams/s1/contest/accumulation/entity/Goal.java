package cn.studyjams.s1.contest.accumulation.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.studyjams.s1.contest.accumulation.util.AppLog;
import cn.studyjams.s1.contest.accumulation.util.MD5;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class Goal extends DataSupport implements Serializable {

    private static final String TAG = "Goal";

    private String goalId;
    private String name;
    private Long time;
    private List<Record> records;

    public Goal() {
        goalId = MD5.md5(new Random().nextLong()+"");
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


    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
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
}
