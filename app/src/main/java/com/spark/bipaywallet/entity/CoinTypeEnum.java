package com.spark.bipaywallet.entity;


import com.spark.bipaywallet.R;

import java.io.Serializable;

public enum CoinTypeEnum implements Serializable {
    BTC(0, "BTC", R.mipmap.btc, 128, 0, "Bitcoin", 1),
    BCH(145, "BCH", R.mipmap.bch, 128, 0, "BitcoinCash", 2),
    ETH(60, "ETH", R.mipmap.eth, -1, -1, "Ethereum", 3),
    LTC(2, "LTC", R.mipmap.ltc, 176, 48, "Litecoin", 4),
    USDT(207, "USDT", R.mipmap.usdt, 128, 0, "Tether", 5),
    XNE(208, "XNE", R.mipmap.xne, 176, 75, "XNE", 6);
    //    DOGE(3, "DOGE", R.mipmap.doge, 176, 30),
    //    DASH(5, "DASH", R.mipmap.dash),
    //    ETC(61, "ETC", R.mipmap.etc),
    //    ZEC(133, "ZEC", R.mipmap.zec),
    //    QTUM(2301, "QTUM", R.mipmap.qtum),
    //    DVC(206, "DVC", R.mipmap.dvc);

    private int type;//币种对应币种码
    private String coinName;//币种名称
    private int resId;//币种图标res
    private int keyPrefix;//币种私钥前缀
    private int addressPrefix;//币种地址前缀
    private String fullName;//币种全称
    private int order;//币种排序值

    public int getType() {
        return type;
    }

    public String getCoinName() {
        return coinName;
    }

    public int getResId() {
        return resId;
    }

    public int getKeyPrefix() {
        return keyPrefix;
    }

    public int getAddressPrefix() {
        return addressPrefix;
    }

    public String getFullName() {
        return fullName;
    }

    public int getOrder() {
        return order;
    }

    CoinTypeEnum(int type, String coinName, int resId, int keyPrefix, int addressPrefix, String fullName, int order) {
        this.type = type;
        this.coinName = coinName;
        this.resId = resId;
        this.keyPrefix = keyPrefix;
        this.addressPrefix = addressPrefix;
        this.fullName = fullName;
        this.order = order;
    }

    public static CoinTypeEnum getCoinTypeEnumByName(String coinName) {
        switch (coinName) {
            case "BTC":
                return BTC;
            case "LTC":
                return LTC;
            case "ETH":
                return ETH;
            case "BCH":
                return BCH;
            case "USDT":
                return USDT;
            case "XNE":
                return XNE;
//            case "DVC":
//                return DVC;
            default:
                return null;
        }
    }

}
