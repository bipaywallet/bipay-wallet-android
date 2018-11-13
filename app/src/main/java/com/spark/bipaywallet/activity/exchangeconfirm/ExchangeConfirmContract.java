package com.spark.bipaywallet.activity.exchangeconfirm;


import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface ExchangeConfirmContract {

    interface View extends Contract.BaseView<Presenter> {

        void getCoinMessageSuccess(HttpCoinMessage obj, String coinName);

        void getCoinMessageFail(Integer code, String toastMessage);

        void getETHMessageSuccess(HttpETHMessage obj, String coinName);

        void getETHMessageFail(Integer code, String toastMessage);

        void getTokenMessageSuccess(Object obj, String contractAddress);

        void getTokenMessageFail(Integer code, String toastMessage);

        void transferPaySuccess(Object obj);

        void transferPayFail(Integer code, String toastMessage);

        void transferPayETHSuccess(Object obj);

        void transferPayETHFail(Integer code, String toastMessage);

        void getServiceChargeSuccess(Object obj);

        void getServiceChargeFail(Integer code, String toastMessage);

        void createTransactionSuccess(Object obj);

        void createTransactionFail(Integer code, String toastMessage);

    }

    interface Presenter extends Contract.BasePresenter {

        void getCoinMessage(HashMap<String, String> params);

        void getETHMessage(HashMap<String, String> params);

        void getTokenMessage(HashMap<String, String> params);

        void transferPay(HashMap<String, String> params);

        void transferPayETH(HashMap<String, String> params);

        void getServiceCharge(HashMap<String, String> params);

        //创建兑换交易
        void createTransaction(String json);
    }

}
