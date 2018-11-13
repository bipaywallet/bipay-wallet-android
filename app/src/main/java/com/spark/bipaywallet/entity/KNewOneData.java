package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class KNewOneData implements Serializable {

//    {
//        "code": "0",
//            "data": {
//        "symbol_period": "btc_60min",
//                "create_time": "2018-08-28 15:53",
//                "close": 6929.84,
//                "open": 6917.64,
//                "low": 6914,
//                "high": 6947.15
//    },
//        "msg": "ok"
//    }

    private String symbol_period;
    private String create_time;
    private float close;
    private float open;
    private float low;
    private float high;

    public String getSymbol_period() {
        return symbol_period;
    }

    public void setSymbol_period(String symbol_period) {
        this.symbol_period = symbol_period;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }


}
