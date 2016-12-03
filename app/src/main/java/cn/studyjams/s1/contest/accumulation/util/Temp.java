package cn.studyjams.s1.contest.accumulation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.entity.Record;

/**
 * 临时数据
 * Created by Relish on 2016/11/10.
 */
public class Temp {

    public static List<Goal> getGoals() {
        Random r = new Random();
        int random = r.nextInt(5) + 3;
        List<Goal> goals = new ArrayList<>();
        for (int i = 0; i < random; i++) {
            Goal goal = new Goal();
            goal.setGoalId(i + 1+"");
            goal.setName("Goal" + (i + 1));
            goal.setRecords(getRecords());
            goals.add(goal);
        }
        return goals;

    }

    public static List<Record> getRecords() {
        Random r = new Random();
        int random = r.nextInt(5) + 3;
        long now = System.currentTimeMillis();
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < random; i++) {
            long t = now + (r.nextInt(1) > 0 ? 1 : -1) * (now % (r.nextInt((int) (now / 12338911)) + 12338911));
            Record record = new Record();
            record.setId(i + 1);
            record.setName("Record" + (i + 1));
            record.setStar(i % 5 + 1);
            record.setCreateTime(TimeUtil.longToDateTime(t));
            record.setNote("You don't know!");
            record.setTime((long) (1000 * (r.nextInt(3600) + 3600)));
            records.add(record);
        }
        return records;
    }

}
