package com.spark.bipaywallet.entity;


import java.io.Serializable;

/**
 * 接口获取的单个币种信息
 */
public class HttpETHMessage implements Serializable {
    private String address;
    private String totalAmount;
    private int nonce;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
}
