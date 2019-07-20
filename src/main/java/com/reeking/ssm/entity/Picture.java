package com.reeking.ssm.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by zhuru on 2018/12/18.
 */
public class Picture implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 图片所在路径
     */
    private String path;

    /**
     * 图片的备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
