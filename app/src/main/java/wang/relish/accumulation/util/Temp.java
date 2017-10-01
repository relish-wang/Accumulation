package wang.relish.accumulation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.entity.User;

/**
 * 临时数据
 * Created by Relish on 2016/11/10.
 */
public class Temp {

    public static List<Goal> getGoals() {
        Random r = new Random();
        int random = r.nextInt(30) + 3;
        List<Goal> goals = new ArrayList<>();
        for (int i = 0; i < random; i++) {
            Goal goal = new Goal();
//            goal.setId(i + 1+"");
            goal.setName("Goal" + (i + 1));
// TODO           goal.setRecords(getRecords());
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

    public static void initDemoData() {
        long userId = 1;
        long goal1Id = 1;
        long goal2Id = 2;
        long goal3Id = 3;
        User user = new User();
        user.setName("Alice");
        user.setPassword("123");
        user.setMobile("13588888888");
        user.setPhoto("/storage/emulated/0/Download/timg.jpg");

        Goal goal1 = new Goal();
        goal1.setId(goal1Id);
        goal1.setName("红楼梦");
        goal1.setUpdateTime("2017-04-30 15:07");
        goal1.setMobile(user.getMobile());
// TODO       goal1.setRecords(getRecords());
// TODO       goal1.save();
    }

    public static List<Record> initHLMRecord() {

        final Record record = new Record();
        record.setGoalId(1);
        record.setName("序");
        record.setStartTime("2017-04-01 10:02");
        record.setEndTime("2017-04-01 13:24");
        long et = TimeUtil.dateTimeToLong(record.getEndTime());
        long st = TimeUtil.dateTimeToLong(record.getStartTime());
        record.setTime(et - st);
        record.setUpdateTime(record.getEndTime());
        record.setNote("序，写的很棒！");
//TODO        record.save();

        final Record record2 = new Record();
        record2.setGoalId(1);
        record2.setName("第一张");
        record2.setStartTime("2017-04-02 09:21");
        record2.setEndTime("2017-04-02 10:42");
        long et2 = TimeUtil.dateTimeToLong(record2.getEndTime());
        long st2 = TimeUtil.dateTimeToLong(record2.getStartTime());
        record2.setTime(et2 - st2);
        record2.setUpdateTime(record2.getEndTime());
        record2.setNote("第一章内容跌宕起伏引人入胜");
//TODO        record2.save();

        return new ArrayList<Record>() {
            {
                add(record);
                add(record2);
            }
        };
    }

}
