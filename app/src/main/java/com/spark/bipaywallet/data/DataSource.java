package com.spark.bipaywallet.data;


import java.util.HashMap;

public interface DataSource {
    void doStringPost(String url, HashMap<String, String> params, DataCallback dataCallback);

    void doStringPost(String url, DataCallback dataCallback);

    void doStringPost(String url, String json, DataCallback dataCallback);

    void getExchangeCoin(String json, DataCallback dataCallback);

    void getCoinMessage(HashMap<String, String> params, CoinDataCallback dataCallback);

    void getETHMessage(HashMap<String, String> params, CoinDataCallback dataCallback);

    void getTokenMessage(HashMap<String, String> params, CoinDataCallback dataCallback);

    void getTokenContract(HashMap<String, String> params, CoinDataCallback dataCallback);

    void getTokenQuery(HashMap<String, String> params, CoinDataCallback dataCallback);

    void transferPay(HashMap<String, String> params, DataCallback dataCallback);

    void transferPayETH(HashMap<String, String> params, DataCallback dataCallback);

    void transactionRecord(HashMap<String, String> params, DataCallback dataCallback);

    void transactionUSDTRecord(HashMap<String, String> params, DataCallback dataCallback);

    void transactionETHRecord(HashMap<String, String> params, DataCallback dataCallback);

    void transactionTokenRecord(HashMap<String, String> params, DataCallback dataCallback);

    void getKuaixunList(HashMap<String, String> params, DataCallback dataCallback);

    void getCurrencyDataList(DataCallback dataCallback);

    void KData(HashMap<String, String> params, DataCallback dataCallback);

    void getNewKOneData(HashMap<String, String> params, DataCallback dataCallback);

    void getServiceCharge(HashMap<String, String> params, DataCallback dataCallback);

    void getVersionMessage(DataCallback dataCallback);

    interface DataCallback {

        void onDataLoaded(Object obj);

        void onDataNotAvailable(Integer code, String toastMessage);
    }

    interface CoinDataCallback {

        void onDataLoaded(Object obj, String coinName);

        void onDataNotAvailable(Integer code, String toastMessage);
    }
}
