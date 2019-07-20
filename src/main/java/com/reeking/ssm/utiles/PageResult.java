package com.reeking.ssm.utiles;

import java.io.Serializable;
import java.util.List;

/**
 * 返回给页面的分页数据
 * Created by zhuru on 2018/12/17.
 */
public class PageResult implements Serializable {

    /**
     * 列表数据
     */
    private List<?> list;

    /**
     * 数据页码
     */
    private int currPage;

    /**
     * 数据总页码
     */
    private int totalPage;

    /**
     * 总记录条数
     */
    private int totalCount;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * @param list
     * @param currPage
     * @param totalCount
     * @param pageSize
     */
    public PageResult(List<?> list, int currPage, int totalCount, int pageSize) {
        this.list = list;
        this.currPage = currPage;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
