package com.randy.randyclient.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RandyZhang on 2017/2/23.
 */

public class BaseListData<BeanType> implements Serializable {

    /**
     * limit : 10
     * page : 1
     * list : [{BeanType},{BeanType}]
     * totalPages : 1
     */

    @SerializedName("limit")
    private int limit;
    @SerializedName("page")
    private int page;
    @SerializedName("totalPages")
    private int totalPages;
    @SerializedName("list")
    private List<BeanType> list;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<BeanType> getList() {
        return list;
    }

    public void setList(List<BeanType> list) {
        this.list = list;
    }


}
