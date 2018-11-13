package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class MyCurrencyData implements Serializable {

//    "name": "BTC",
//            "c_name": "比特币",
//            "open": "6480",
//            "close": "6390.9",
//            "rise": "-1.38",
//            "close_rmb": "43777.67",
//            "logo_url": "http:\/\/www.qkljw.com\/Public\/Home\/images\/coin\/btc.png"

    private String name;
    private String c_name;
    private String open;
    private String close;
    private String rise;
    private String close_rmb;
    private String logo_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }

    public String getClose_rmb() {
        return close_rmb;
    }

    public void setClose_rmb(String close_rmb) {
        this.close_rmb = close_rmb;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
}
