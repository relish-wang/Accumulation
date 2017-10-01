package wang.relish.accumulation.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import wang.relish.accumulation.util.TimeUtil;

/**
 * 记录
 * Created by Relish on 2016/11/10.
 */
@Entity
public class Record implements Serializable {

    public static final long serialVersionUID = 536871012L;

    @Id
    private long id;
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
        goalId = -1;//未分类
        name = "[未命名]";
        star = 1;
        note = "";
        time = 0L;
        createTime = TimeUtil.getNowTime();
        startTime = createTime;
        updateTime = createTime;
    }

    @Generated(hash = 84132456)
    public Record(long id, long goalId, String name, Integer star, String note,
                  Long time, String createTime, String startTime, String endTime,
                  String updateTime) {
        this.id = id;
        this.goalId = goalId;
        this.name = name;
        this.star = star;
        this.note = note;
        this.time = time;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.updateTime = updateTime;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGoalId() {
        return this.goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStar() {
        return this.star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}