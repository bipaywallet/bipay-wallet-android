package com.spark.bipaywallet.entity;


public class SendParam {
    private int pageIndex;
    private int pageSize;
    private String[] queryList = new String[]{};
    private String sortFields;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String[] getQueryList() {
        return queryList;
    }

    public void setQueryList(String[] queryList) {
        this.queryList = queryList;
    }

    public String getSortFields() {
        return sortFields;
    }

    public void setSortFields(String sortFields) {
        this.sortFields = sortFields;
    }
}
