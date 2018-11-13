package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class TokenQuery implements Serializable {
//    "data": {
//                "decimals": "6",
//                "symbol": "MITK",
//                "address": "0xd1e4b75190349e1fc9dbc77c71999c0461407526",
//                "eventTopic0": null
//    }

    private String decimals;
    private String symbol;
    private String address;
    private String eventTopic0;

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEventTopic0() {
        return eventTopic0;
    }

    public void setEventTopic0(String eventTopic0) {
        this.eventTopic0 = eventTopic0;
    }

}
