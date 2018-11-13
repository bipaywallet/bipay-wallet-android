package com.spark.bipaywallet.data;


import java.util.HashMap;

public class DataRepository implements DataSource {
    private static DataRepository INSTANCE = null;
    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;
    private boolean isLocal = false;

    private DataRepository(DataSource mRemoteDataSource, DataSource mLocalDataSource) {
        this.mRemoteDataSource = mRemoteDataSource;
        this.mLocalDataSource = mLocalDataSource;
    }

    public static DataRepository getInstance(DataSource mRemoteDataSource, DataSource mLocalDataSource) {
        if (INSTANCE == null) INSTANCE = new DataRepository(mRemoteDataSource, mLocalDataSource);
        return INSTANCE;
    }

    @Override
    public void doStringPost(String url, HashMap<String, String> map, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.doStringPost(url, map, dataCallback);
        else mRemoteDataSource.doStringPost(url, map, dataCallback);
    }

    @Override
    public void doStringPost(String url, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.doStringPost(url, dataCallback);
        else mRemoteDataSource.doStringPost(url, dataCallback);
    }

    @Override
    public void doStringPost(String url, String json, DataCallback dataCallback) {
        if (isLocal) doStringPost(url, json, dataCallback);
        else mRemoteDataSource.doStringPost(url, json, dataCallback);
    }

    @Override
    public void getExchangeCoin(String json, DataCallback dataCallback) {
        if (isLocal) getExchangeCoin(json, dataCallback);
        else mRemoteDataSource.getExchangeCoin(json, dataCallback);
    }

    @Override
    public void getCoinMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getCoinMessage(params, dataCallback);
        else mRemoteDataSource.getCoinMessage(params, dataCallback);
    }

    @Override
    public void getETHMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getETHMessage(params, dataCallback);
        else mRemoteDataSource.getETHMessage(params, dataCallback);
    }

    @Override
    public void getTokenMessage(HashMap<String, String> params, CoinDataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getTokenMessage(params, dataCallback);
        else mRemoteDataSource.getTokenMessage(params, dataCallback);
    }

    @Override
    public void getTokenContract(HashMap<String, String> params, CoinDataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getTokenContract(params, dataCallback);
        else mRemoteDataSource.getTokenContract(params, dataCallback);
    }

    @Override
    public void getTokenQuery(HashMap<String, String> params, CoinDataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getTokenQuery(params, dataCallback);
        else mRemoteDataSource.getTokenQuery(params, dataCallback);
    }

    @Override
    public void transferPay(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transferPay(params, dataCallback);
        else mRemoteDataSource.transferPay(params, dataCallback);
    }

    @Override
    public void transferPayETH(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transferPayETH(params, dataCallback);
        else mRemoteDataSource.transferPayETH(params, dataCallback);
    }

    @Override
    public void transactionRecord(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transactionRecord(params, dataCallback);
        else mRemoteDataSource.transactionRecord(params, dataCallback);
    }

    @Override
    public void transactionUSDTRecord(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transactionUSDTRecord(params, dataCallback);
        else mRemoteDataSource.transactionUSDTRecord(params, dataCallback);
    }

    @Override
    public void transactionETHRecord(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transactionETHRecord(params, dataCallback);
        else mRemoteDataSource.transactionETHRecord(params, dataCallback);
    }

    @Override
    public void transactionTokenRecord(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.transactionTokenRecord(params, dataCallback);
        else mRemoteDataSource.transactionTokenRecord(params, dataCallback);
    }

    @Override
    public void getKuaixunList(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getKuaixunList(params, dataCallback);
        else mRemoteDataSource.getKuaixunList(params, dataCallback);
    }

    @Override
    public void getCurrencyDataList(DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getCurrencyDataList(dataCallback);
        else mRemoteDataSource.getCurrencyDataList(dataCallback);
    }

    @Override
    public void KData(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.KData(params, dataCallback);
        else mRemoteDataSource.KData(params, dataCallback);
    }

    @Override
    public void getNewKOneData(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getNewKOneData(params, dataCallback);
        else mRemoteDataSource.getNewKOneData(params, dataCallback);
    }

    @Override
    public void getServiceCharge(HashMap<String, String> params, DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getServiceCharge(params, dataCallback);
        else mRemoteDataSource.getServiceCharge(params, dataCallback);
    }

    @Override
    public void getVersionMessage(DataCallback dataCallback) {
        if (isLocal) mLocalDataSource.getVersionMessage(dataCallback);
        else mRemoteDataSource.getVersionMessage(dataCallback);
    }
}
