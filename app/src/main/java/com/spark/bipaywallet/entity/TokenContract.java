package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class TokenContract implements Serializable {
//    "data": {
//                "decimals": "6",
//                "symbol": "MITK",
//                "address": "0xd1e4b75190349e1fc9dbc77c71999c0461407526",
//                "eventTopic0": null
//    }

//    "data": {
//                "name": "GIP",
//                "cnyRate": 6,
//                "ustRate": 1,
//                "contractAddress": "0x0b42c73446e4090a7c1db8ac00ad46a38ccbc2ac",
//                "imgUrl": "http://xinhuo-xindai.oss-cn-hangzhou.aliyuncs.com/2018/10/15/0aba701b-890d-4652-9e8e-02204d9fb47d.jpg",
//                "decimals": "4"
//    }

    private String name;
    private String cnyRate;
    private String ustRate;
    private String imgUrl;
    private String contractAddress;
    private String decimals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnyRate() {
        return cnyRate;
    }

    public void setCnyRate(String cnyRate) {
        this.cnyRate = cnyRate;
    }

    public String getUstRate() {
        return ustRate;
    }

    public void setUstRate(String ustRate) {
        this.ustRate = ustRate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }


}
