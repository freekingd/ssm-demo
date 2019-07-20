package com.reeking.ssm.utiles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhuru on 2018/12/17.
 */
public class PageUtil extends LinkedHashMap<String, Object> {

    // 当前页码
    private int page;

    // 每页条数
    private int limit;

    public PageUtil(Map<String, Object> params) {
        this.putAll(params);
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("start", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
