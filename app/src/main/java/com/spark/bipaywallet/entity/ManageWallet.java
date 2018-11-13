package com.spark.bipaywallet.entity;


import java.io.Serializable;

/**
 * 钱包
 */
public class ManageWallet implements Serializable {
    private String name;//钱包名称
    private String allMoney;//总资产
    private String initial;//首字母

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(String allMoney) {
        this.allMoney = allMoney;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }
}
