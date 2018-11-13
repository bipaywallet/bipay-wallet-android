package com.spark.bipaywallet.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 本地保存的兑换交易记录
 */
public class MyExchangeRecord extends DataSupport implements Serializable {

    private String walletName;
    private String exchangeId;
    private String time;
    private String status;//状态
    private String currencyFrom;//转出币种
    private String currencyTo;//收款币种
    private String payinAddress;//收款地址
    private String amountExpectedFrom;////转出金额
    private String payoutAddress;//转出地址
    private String amountExpectedTo;//收款金额
    private String rate;

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getAmountExpectedTo() {
        return amountExpectedTo;
    }

    public void setAmountExpectedTo(String amountExpectedTo) {
        this.amountExpectedTo = amountExpectedTo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
