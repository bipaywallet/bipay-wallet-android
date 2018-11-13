package com.spark.bipaywallet.activity.payment;


import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface PaymentContract {

    interface View extends Contract.BaseView<Presenter> {
        void getCoinMessageSuccess(HttpCoinMessage obj, String coinName);

        void getCoinMessageFail(Integer code, String toastMessage);

        void getETHMessageSuccess(HttpETHMessage obj, String coinName);

        void getETHMessageFail(Integer code, String toastMessage);

        void getTokenMessageSuccess(Object obj, String contractAddress);

        void getTokenMessageFail(Integer code, String toastMessage);

        void transactionRecordSuccess(Object obj);

        void transactionRecordFail(Integer code, String toastMessage);

        void transactionUSDTRecordSuccess(Object obj);

        void transactionUSDTRecordFail(Integer code, String toastMessage);

        void transactionETHRecordSuccess(Object obj);

        void transactionETHRecordFail(Integer code, String toastMessage);

        void transactionTokenRecordSuccess(Object obj);

        void transactionTokenRecordFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {
        void getCoinMessage(HashMap<String, String> params);

        void getETHMessage(HashMap<String, String> params);

        void getTokenMessage(HashMap<String, String> params);

        void transactionRecord(HashMap<String, String> params);

        void transactionUSDTRecord(HashMap<String, String> params);

        void transactionETHRecord(HashMap<String, String> params);

        void transactionTokenRecord(HashMap<String, String> params);
    }

}
