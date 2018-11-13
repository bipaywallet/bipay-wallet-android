package com.spark.bipaywallet.activity.payment;


import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public class PaymentPresenter implements PaymentContract.Presenter {
    private DataSource dataRepository;
    private PaymentContract.View view;

    public PaymentPresenter(DataSource dataRepository, PaymentContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void getCoinMessage(HashMap<String, String> params) {
        dataRepository.getCoinMessage(params, new DataSource.CoinDataCallback() {
            @Override
            public void onDataLoaded(Object obj, String coinName) {
                view.getCoinMessageSuccess((HttpCoinMessage) obj, coinName);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getCoinMessageFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getETHMessage(HashMap<String, String> params) {
        dataRepository.getETHMessage(params, new DataSource.CoinDataCallback() {
            @Override
            public void onDataLoaded(Object obj, String coinName) {
                view.getETHMessageSuccess((HttpETHMessage) obj, coinName);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getETHMessageFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getTokenMessage(HashMap<String, String> params) {
        dataRepository.getTokenMessage(params, new DataSource.CoinDataCallback() {
            @Override
            public void onDataLoaded(Object obj, String contractAddress) {
                view.getTokenMessageSuccess(obj, contractAddress);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getTokenMessageFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transactionRecord(HashMap<String, String> params) {
        dataRepository.transactionRecord(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.transactionRecordSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.transactionRecordFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transactionUSDTRecord(HashMap<String, String> params) {
        dataRepository.transactionUSDTRecord(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.transactionUSDTRecordSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.transactionUSDTRecordFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transactionETHRecord(HashMap<String, String> params) {
        dataRepository.transactionETHRecord(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.transactionETHRecordSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.transactionETHRecordFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transactionTokenRecord(HashMap<String, String> params) {
        dataRepository.transactionTokenRecord(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.transactionTokenRecordSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.transactionTokenRecordFail(code, toastMessage);
            }
        });
    }


}
