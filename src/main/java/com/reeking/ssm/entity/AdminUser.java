package com.reeking.ssm.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by zhuru on 2018/12/16.
 */
public class AdminUser implements Serializable {

    /**
     *  主键
     */
    private Long id;

    /**
     *  用户名
     */
    private String userName;

    /**
     *  密码
     */
    private String password;

    /**
     *  用户token
     */
    private String userToken;

    /**
     *  是否被删除   0未删除    1已删除
     */
    private int isDeleted;

    /**
     *  创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userToken='" + userToken + '\'' +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                '}';
    }
}
