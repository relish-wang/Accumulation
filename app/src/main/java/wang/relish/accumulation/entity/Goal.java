package wang.relish.accumulation.entity;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
@Entity
public class Goal implements Serializable {

    public static final long serialVersionUID = 536871008L;

    @Id(autoincrement = true)
    private long id;
    @NotNull
    private String mobile;
    @Property(nameInDb = "name")
    private String name;
    @NotNull
    private long time;
    @NonNull
    private String updateTime;

    @Generated(hash = 1367492882)
    public Goal(long id, @NotNull String mobile, String name, long time,
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

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
