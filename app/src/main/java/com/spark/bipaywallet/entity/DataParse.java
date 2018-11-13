package com.spark.bipaywallet.entity;

import android.util.SparseArray;

import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.utils.DateUtils;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/2/10.
 */

public class DataParse implements Serializable {
    private ArrayList<MinutesBean> datas = new ArrayList<>();
    private ArrayList<KLineBean> kDatas = new ArrayList<>();
    private ArrayList<String> xVals = new ArrayList<>();//X轴数据
    private float baseValue;
    private float permaxmin;
    private float volmax;
    private SparseArray<String> xValuesLabel = new SparseArray<>();


    public void parseMinutes(JSONArray object, float baseValue) {
        if (object == null) return;
        /*数据解析依照自己需求来定，如果服务器直接返回百分比数据，则不需要客户端进行计算*/
        this.baseValue = baseValue;
        int count = object.length();
        for (int i = 0; i < count; i++) {
            JSONArray data = object.optJSONArray(i);
            MinutesBean minutesData = new MinutesBean();
            minutesData.open = (float) data.optDouble(1);
            minutesData.close = (float) data.optDouble(4);
            minutesData.high = (float) data.optDouble(2);
            minutesData.low = (float) data.optDouble(3);

            minutesData.time = DateUtils.getFormatTime("HH:mm", new Date(data.optLong(0)));
            minutesData.cjprice = (float) data.optDouble(4);
            minutesData.cjnum = (float) data.optDouble(5);
            minutesData.total = minutesData.cjnum * minutesData.cjprice;
            minutesData.avprice = minutesData.cjprice;
            minutesData.cha = minutesData.cjprice - baseValue;
            minutesData.per = (minutesData.cha / baseValue);
            double cha = minutesData.cjprice - baseValue;
            if (Math.abs(cha) > permaxmin) {
                permaxmin = (float) Math.abs(cha);
            }
            volmax = Math.max(minutesData.cjnum, volmax);
            datas.add(minutesData);
        }
        if (permaxmin == 0) {
            permaxmin = baseValue * 0.02f;
        }
    }

    public void parseMinutes(List<KDataMessage.KMessageBean> objList) {
        /*数据解析依照自己需求来定，如果服务器直接返回百分比数据，则不需要客户端进行计算*/
//        this.baseValue = baseValue;
        int count = objList.size();
        for (int i = 0; i < count; i++) {
            KDataMessage.KMessageBean kMessageBean = objList.get(i);

            MinutesBean minutesData = new MinutesBean();
            minutesData.open = kMessageBean.getOpen();
            minutesData.close = kMessageBean.getClose();
            minutesData.high = kMessageBean.getHigh();
            minutesData.low = kMessageBean.getLow();

            minutesData.time = kMessageBean.getCreate_time();
            minutesData.cjprice = kMessageBean.getClose();
//            minutesData.cjnum = (float) data.optDouble(5);
//            minutesData.total = minutesData.cjnum * minutesData.cjprice;
            minutesData.avprice = minutesData.cjprice;
//            minutesData.cha = minutesData.cjprice - baseValue;
//            minutesData.per = (minutesData.cha / baseValue);
//            double cha = minutesData.cjprice - baseValue;
//            if (Math.abs(cha) > permaxmin) {
//                permaxmin = (float) Math.abs(cha);
//            }
//            volmax = Math.max(minutesData.cjnum, volmax);
            datas.add(minutesData);
        }
//        if (permaxmin == 0) {
//            permaxmin = baseValue * 0.02f;
//        }
    }

    public void parseKLine(List<KDataMessage.KMessageBean> objList, int tag) {
        for (int i = 0, len = objList.size(); i < len; i++) {
            KDataMessage.KMessageBean kMessageBean = objList.get(i);
            String date;
            if (tag == GlobalConstant.TAG_FIVE_MINUTE || tag == GlobalConstant.TAG_AN_HOUR || tag == GlobalConstant.TAG_THIRTY_MINUTE) {
                String str = kMessageBean.getCreate_time();
                date = str.substring(5);
            } else {
                date = kMessageBean.getCreate_time();
            }

            //K线实体类
            KLineBean kLineData = new KLineBean(date, kMessageBean.getOpen(), kMessageBean.getClose(),
                    kMessageBean.getHigh(), kMessageBean.getLow(), 0.0f);
            kDatas.add(kLineData);
            volmax = Math.max(kLineData.vol, volmax);
            xValuesLabel.put(i, kLineData.date);
        }
    }


    public ArrayList<MinutesBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<MinutesBean> datas) {
        this.datas = datas;
    }

    public ArrayList<KLineBean> getKLineDatas() {
        return kDatas;
    }

    public void setkDatas(ArrayList<KLineBean> kDatas) {
        this.kDatas = kDatas;
    }

    public ArrayList<String> getxVals() {
        return xVals;
    }

    public void setxVals(ArrayList<String> xVals) {
        this.xVals = xVals;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
    }

    public float getPermaxmin() {
        return permaxmin;
    }

    public void setPermaxmin(float permaxmin) {
        this.permaxmin = permaxmin;
    }

    public float getVolmax() {
        return volmax;
    }

    public void setVolmax(float volmax) {
        this.volmax = volmax;
    }

    public SparseArray<String> getxValuesLabel() {
        return xValuesLabel;
    }

    public void setxValuesLabel(SparseArray<String> xValuesLabel) {
        this.xValuesLabel = xValuesLabel;
    }

    /**
     * 得到Y轴最小值
     */
    public float getMin() {
        return baseValue - permaxmin;
    }

    /**
     * 得到Y轴最大值
     */
    public float getMax() {
        return baseValue + permaxmin;
    }

    /**
     * 得到百分百最大值
     *
     * @return
     */
    public float getPercentMax() {
        return permaxmin / baseValue;
    }

    /**
     * 得到百分比最小值
     *
     * @return
     */
    public float getPercentMin() {
        return -getPercentMax();
    }


}
