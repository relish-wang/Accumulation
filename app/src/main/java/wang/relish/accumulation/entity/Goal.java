package wang.relish.accumulation.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import wang.relish.accumulation.App;
import wang.relish.accumulation.greendao.RecordDao;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
@Entity
public class Goal implements Serializable {

    public static final long serialVersionUID = 536871008L;

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String mobile;
    @Property(nameInDb = "name")
    private String name;
    @NotNull
    private String time;
    @NotNull
    private String updateTime;

    @Generated(hash = 29737640)
    public Goal(Long id, @NotNull String mobile, String name, @NotNull String time,
                @NotNull String updateTime) {
        this.id = id;
        this.mobile = mobile;
        this.name = name;
        this.time = time;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1149104271)
    public Goal() {
    }

    public Long getId() {
        return this.id;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
        List<Record> records = getRecords();
        for (Record record : records) {
            time += record.getTime();
        }
        return time;
    }

    public List<Record> getRecords() {
        return App.getDaosession()
                .getRecordDao()
                .queryBuilder()
                .where(RecordDao.Properties.GoalId.eq(id))
                .list();
    }

    public void setId(Long id) {
        this.id = id;
    }
}
