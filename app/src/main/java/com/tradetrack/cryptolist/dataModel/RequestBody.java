package com.tradetrack.cryptolist.dataModel;

import com.google.gson.annotations.SerializedName;

public class RequestBody {
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("sort")
    private String sort;
    
    @SerializedName("order")
    private String order;
    
    @SerializedName("offset")
    private int offset;
    
    @SerializedName("limit")
    private int limit;
    
    @SerializedName("meta")
    private boolean meta;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }
}
