package com.github.tifezh.kchartlib.chart.entity;

import java.util.Date;

/**
 * 分时图实体接口
 * Created by tifezh on 2017/7/19.
 */

public interface IMinuteLine {

    /**
     * @return 获取均价
     */
    float getAvgPrice();

    /**
     * @return 获取成交价
     */
    float getPrice();

    /**
     * 该指标对应的时间
     */
    Date getDate();

    /**
     * 成交量
     */
    float getVolume();

    /**
     * 二十(月，日，时，分，5分等)均价
     */
    float getMA30Price();

    /**
     * 收盘价
     */
    float getClosePrice();

    /**
     * 绘制Boll
     */
    float getMb();

}
