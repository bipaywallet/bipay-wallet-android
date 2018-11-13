package com.spark.bipaywallet.entity;


import java.io.Serializable;
import java.util.List;

public class KDataMessage implements Serializable {
    private String symbol_period;
    private List<KMessageBean> kline_data;

    public String getSymbol_period() {
        return symbol_period;
    }

    public void setSymbol_period(String symbol_period) {
        this.symbol_period = symbol_period;
    }

    public List<KMessageBean> getKline_data() {
        return kline_data;
    }

    public void setKline_data(List<KMessageBean> kline_data) {
        this.kline_data = kline_data;
    }

    public static class KMessageBean implements Serializable {
        private String create_time;
        private float close;
        private float open;
        private float low;
        private float high;

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


}
