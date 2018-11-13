package com.spark.bipaywallet.entity;


import java.io.Serializable;

/**
 * 接口获取的兑换交易记录
 */
public class HttpExchangeRecord implements Serializable {
//             "id": "8zydqkq4y7z5eawm",
//             "createdAt": 1540526514,
//             "moneyReceived": 0,
//             "moneySent": 0,
//             "payinConfirmations": "0",
//             "status": "waiting",
//             "currencyFrom": "btc",
//             "currencyTo": "eth",
//             "payinAddress": "36FzJXXEFL32BwFBKEJ6uhcibj4JmTi3Pc",
//             "payinExtraId": null,
//             "payinHash": null,
//             "amountExpectedFrom": "2",
//             "payoutAddress": "0x7aa1ff2abfa0e7e282f8a2f14105cd4e5d8def0a",
//             "payoutExtraId": null,
//             "payoutHash": null,
//             "refundHash": null,
//             "amountFrom": "",
//             "amountTo": "0",
//             "amountExpectedTo": "63.68995",
//             "networkFee": null,
//             "changellyFee": "0.5",
//             "apiExtraFee": "0"

    private String id;
    private String createdAt;
    private String moneyReceived;
    private String moneySent;
    private String payinConfirmations;
    private String status;
    private String currencyFrom;
    private String currencyTo;
    private String payinAddress;
    private String payinExtraId;
    private String payinHash;
    private String amountExpectedFrom;
    private String payoutAddress;
    private String payoutExtraId;
    private String payoutHash;
    private String refundHash;
    private String amountFrom;
    private String amountTo;
    private String amountExpectedTo;
    private String networkFee;
    private String changellyFee;
    private String apiExtraFee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMoneyReceived() {
        return moneyReceived;
    }

    public void setMoneyReceived(String moneyReceived) {
        this.moneyReceived = moneyReceived;
    }

    public String getMoneySent() {
        return moneySent;
    }

    public void setMoneySent(String moneySent) {
        this.moneySent = moneySent;
    }

    public String getPayinConfirmations() {
        return payinConfirmations;
    }

    public void setPayinConfirmations(String payinConfirmations) {
        this.payinConfirmations = payinConfirmations;
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

    public String getPayinAddress() {
        return payinAddress;
    }

    public void setPayinAddress(String payinAddress) {
        this.payinAddress = payinAddress;
    }

    public String getPayinExtraId() {
        return payinExtraId;
    }

    public void setPayinExtraId(String payinExtraId) {
        this.payinExtraId = payinExtraId;
    }

    public String getPayinHash() {
        return payinHash;
    }

    public void setPayinHash(String payinHash) {
        this.payinHash = payinHash;
    }

    public String getAmountExpectedFrom() {
        return amountExpectedFrom;
    }

    public void setAmountExpectedFrom(String amountExpectedFrom) {
        this.amountExpectedFrom = amountExpectedFrom;
    }

    public String getPayoutAddress() {
        return payoutAddress;
    }

    public void setPayoutAddress(String payoutAddress) {
        this.payoutAddress = payoutAddress;
    }

    public String getPayoutExtraId() {
        return payoutExtraId;
    }

    public void setPayoutExtraId(String payoutExtraId) {
        this.payoutExtraId = payoutExtraId;
    }

    public String getPayoutHash() {
        return payoutHash;
    }

    public void setPayoutHash(String payoutHash) {
        this.payoutHash = payoutHash;
    }

    public String getRefundHash() {
        return refundHash;
    }

    public void setRefundHash(String refundHash) {
        this.refundHash = refundHash;
    }

    public String getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(String amountFrom) {
        this.amountFrom = amountFrom;
    }

    public String getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(String amountTo) {
        this.amountTo = amountTo;
    }

    public String getAmountExpectedTo() {
        return amountExpectedTo;
    }

    public void setAmountExpectedTo(String amountExpectedTo) {
        this.amountExpectedTo = amountExpectedTo;
    }

    public String getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(String networkFee) {
        this.networkFee = networkFee;
    }

    public String getChangellyFee() {
        return changellyFee;
    }

    public void setChangellyFee(String changellyFee) {
        this.changellyFee = changellyFee;
    }

    public String getApiExtraFee() {
        return apiExtraFee;
    }

    public void setApiExtraFee(String apiExtraFee) {
        this.apiExtraFee = apiExtraFee;
    }
}
