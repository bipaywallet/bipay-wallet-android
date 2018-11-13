package com.spark.bipaywallet.factory;

/**
 * Created by Administrator on 2018/1/29.
 */

public class UrlFactory {
    //    private static final String host = "http://36.7.147.3:9333";
    private static final String host = "http://api.bipay.wxmarket.cn:9333";

    //查询某一地址列表余额（utxo）
    public static String getCoinMessage(String coinName) {
        if (coinName != null && coinName.equalsIgnoreCase("usdt")) {
            return host + "/omni/address/single-address/unspent/tx";
        } else {
            return host + "/" + coinName + "/address/single-address/unspent/tx";
        }
    }

    //查询Token地址余额
    public static String getTokenCoinMessage(String coinName) {
        return host + "/" + coinName + "/address/token/single-address/unspent/tx";
    }

    //Token合约symbol
    public static String getTokenQuery(String coinName) {
        return host + "/" + coinName + "/address/token/query";
    }

    //Token合约
    public static String getTokenContract(String coinName) {
        return host + "/toc/coin/token/query/detail";
    }

    //查询交易手续费
    public static String getServiceCharge(String coinName) {
        return host + "/" + coinName + "/raw/get/service-charge";
    }

    //转账，创建一笔交易
    public static String getTransferPayUrl(String coinName) {
        return host + "/" + coinName + "/raw/send";
    }

    //查询某一地址列表的交易记录
    public static String getTransactionRecordUrl(String coinName) {
        if (coinName != null && coinName.equalsIgnoreCase("usdt")) {
            return host + "/omni/address/single-address/total/tx";
        } else {
            return host + "/" + coinName + "/address/single-address/total/tx";
        }
    }

    //查询Token地址的交易记录
    public static String getTokenTransactionRecordUrl(String coinName) {
        return host + "/" + coinName + "/address/token/single-address/total/tx";
    }

    //快讯
    public static String getKuaixunUrl() {
        return "http://www.qkljw.com/app/index/get_kuaixun_list";
    }

    //获取快讯新消息
    public static String getCount() {
        return "http://www.qkljw.com/app/index/get_new_kuaixun_count.html";
    }

    //首页获取币种数据
    public static String getCurrencyDataUrl() {
        return "http://www.qkljw.com/app/Kline/get_currency_data";
    }

    //币种对应的K线数据
    public static String getKDataUrl() {
        return "http://www.qkljw.com/app/Kline/get_kline";
    }

    //K线类型对应最新一条数据
    public static String getNewKOneData() {
        return "http://www.qkljw.com/app/Kline/get_newest_data";
    }

    //首页获取公告
    public static String getNoticeUrl() {
        return "http://om.xinhuokj.com/getway/api/cms/notice/findBy";
    }

    //帮助中心
    public static String getHelpUrl() {
        return "http://om.xinhuokj.com/getway/api/cms/article/findBy";
    }

    //版本更新
    public static String getVersionUrl() {
        return "http://om.xinhuokj.com/getway/api/cms/appInfo/findInfo";
    }

    //币币兑换
    public static String getExchangeUrl() {
        return "https://api.changelly.com";
    }


}
