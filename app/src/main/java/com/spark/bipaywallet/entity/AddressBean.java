package com.spark.bipaywallet.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class AddressBean extends DataSupport implements Serializable {
    private String linkManName;//对应联系人名称
    private String coinName;//对应币种
    private String address;//地址

    public String getLinkManName() {
        return linkManName;
    }

    public void setLinkManName(String linkManName) {
        this.linkManName = linkManName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
