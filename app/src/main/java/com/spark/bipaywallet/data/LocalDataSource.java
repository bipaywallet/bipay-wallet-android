package com.spark.bipaywallet.data;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * 当需要使用缓存等时 需要使用Local该类加载数据
 */

public class LocalDataSource implements DataSource {
    private static LocalDataSource INSTANCE;
    private Handler handler = new Handler(Looper.getMainLooper());

    public LocalDataSource(Context context) {
    }

    public static LocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void doStringPost(String url, HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void doStringPost(String url, DataCallback dataCallback) {

    }

    @Override
    public void doStringPost(String url, String json, DataCallback dataCallback) {

    }

    @Override
    public void getExchangeCoin(String json, DataCallback dataCallback) {

    }

    @Override
    public void getCoinMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {

    }

    @Override
    public void getETHMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {

    }

    @Override
    public void getTokenMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {

    }

    @Override
    public void getTokenContract(HashMap<String, String> params, CoinDataCallback dataCallback) {

    }

    @Override
    public void getTokenQuery(HashMap<String, String> params, CoinDataCallback dataCallback) {

    }

    @Override
    public void transactionTokenRecord(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void transferPay(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void transferPayETH(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void transactionRecord(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void transactionUSDTRecord(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void transactionETHRecord(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void getKuaixunList(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void getCurrencyDataList(DataCallback dataCallback) {

    }

    @Override
    public void KData(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void getNewKOneData(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void getServiceCharge(HashMap<String, String> params, DataCallback dataCallback) {

    }

    @Override
    public void getVersionMessage(DataCallback dataCallback) {

    }
}
