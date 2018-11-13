package com.spark.bipaywallet.entity;


import java.io.Serializable;

/**
 * 接口创建的兑换交易
 */
public class HttpExchangeTransaction implements Serializable {
//            "id": "jev5lt0qmg26h48v",
//            "apiExtraFee": "0",
//            "changellyFee": "0.5",
//            "payinExtraId": null,
//            "amountExpectedFrom": 1,
//            "status": "new",
//            "currencyFrom": "eth",
//            "currencyTo": "ltc",
//            "amountTo": 0,
//            "payinAddress": "<<doge address to send coins to>>",
//            "payoutAddress": "<<valid ltc address>>",
//            "createdAt": "2018-09-24T10:31:18.000Z"

    private String id;
    private String apiExtraFee;
    private String changellyFee;
    private String payinExtraId;
    private String amountExpectedFrom;
    private String status;
    private String currencyFrom;
    private String currencyTo;
    private String amountTo;
    private String payinAddress;
    private String payoutAddress;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiExtraFee() {
        return apiExtraFee;
    }

    public void setApiExtraFee(String apiExtraFee) {
        this.apiExtraFee = apiExtraFee;
    }

    public String getChangellyFee() {
        return changellyFee;
    }

    public void setChangellyFee(String changellyFee) {
        this.changellyFee = changellyFee;
    }

    public String getPayinExtraId() {
        return payinExtraId;
    }

    public void setPayinExtraId(String payinExtraId) {
        this.payinExtraId = payinExtraId;
    }

    public String getAmountExpectedFrom() {
        return amountExpectedFrom;
    }

    public void setAmountExpectedFrom(String amountExpectedFrom) {
        this.amountExpectedFrom = amountExpectedFrom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public String getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(String amountTo) {
        this.amountTo = amountTo;
    }

    public String getPayinAddress() {
        return payinAddress;
    }

    public void setPayinAddress(String payinAddress) {
        this.payinAddress = payinAddress;
    }

    public String getPayoutAddress() {
        return payoutAddress;
    }

    public void setPayoutAddress(String payoutAddress) {
        this.payoutAddress = payoutAddress;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
