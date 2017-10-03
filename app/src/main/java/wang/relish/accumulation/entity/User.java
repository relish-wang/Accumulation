package wang.relish.accumulation.entity;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 用户实体类
 * Created by Relish on 2016/12/3.
 */
@Entity
public class User implements Serializable {

    public static final long serialVersionUID = 536871016L;

    private String name;
    private String password;

    @Id
    private String mobile;
    private String photo;

    @Generated(hash = 574889579)
    public User(String name, String password, String mobile, String photo) {
        this.name = name;
        this.password = password;
        this.mobile = mobile;
        this.photo = photo;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(password) &&
                TextUtils.isEmpty(mobile) &&
                TextUtils.isEmpty(photo);
    }
}
