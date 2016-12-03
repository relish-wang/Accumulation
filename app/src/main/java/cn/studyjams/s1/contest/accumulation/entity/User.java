package cn.studyjams.s1.contest.accumulation.entity;

import android.text.TextUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 用户实体类
 * Created by Relish on 2016/12/3.
 */
public class User extends DataSupport implements Serializable {

    private String name;
    private String password;
    private String email;
    private String mobile;
    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(password) &&
                TextUtils.isEmpty(email) &&
                TextUtils.isEmpty(mobile) &&
                TextUtils.isEmpty(photo);
    }
}
