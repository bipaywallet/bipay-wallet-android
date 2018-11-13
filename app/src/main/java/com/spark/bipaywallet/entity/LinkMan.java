package com.spark.bipaywallet.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class LinkMan extends DataSupport implements Serializable {
    private String name;//姓名
    private String phoneNumber;//手机号
    private String email;//邮箱
    private String remarks;//备注
    private String initial;//首字母

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }
}
