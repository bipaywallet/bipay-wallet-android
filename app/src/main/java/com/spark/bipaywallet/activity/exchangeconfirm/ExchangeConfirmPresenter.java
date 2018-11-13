package com.spark.bipaywallet.activity.exchangeconfirm;


import com.google.gson.Gson;
import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.HttpExchangeTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public class ExchangeConfirmPresenter implements ExchangeConfirmContract.Presenter {
    private DataSource dataRepository;
    private ExchangeConfirmContract.View view;

    public ExchangeConfirmPresenter(DataSource dataRepository, ExchangeConfirmContract.View view) {
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
    public void transferPay(HashMap<String, String> params) {
        view.displayLoadingPopup();
        dataRepository.transferPay(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.transferPaySuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.transferPayFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transferPayETH(HashMap<String, String> params) {
        view.displayLoadingPopup();
        dataRepository.transferPayETH(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.transferPayETHSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.transferPayETHFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getServiceCharge(HashMap<String, String> params) {
        dataRepository.getServiceCharge(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.getServiceChargeSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getServiceChargeFail(code, toastMessage);
            }
        });
    }

    @Override
    public void createTransaction(String json) {
        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    HttpExchangeTransaction httpExchangeTransaction = new Gson().fromJson(object.getJSONObject("result").toString(), HttpExchangeTransaction.class);
                    view.createTransactionSuccess(httpExchangeTransaction);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.createTransactionFail(code, toastMessage);
            }
        });
    }

}
