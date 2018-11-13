package com.spark.bipaywallet.activity.transferpay;


import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public class TransferPayPresenter implements TransferPayContract.Presenter {
    private DataSource dataRepository;
    private TransferPayContract.View view;

    public TransferPayPresenter(DataSource dataRepository, TransferPayContract.View view) {
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

}
