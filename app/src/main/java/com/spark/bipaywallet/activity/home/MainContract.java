package com.spark.bipaywallet.activity.home;


import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCurrencyData;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.entity.VersionMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface MainContract {

    interface View extends Contract.BaseView<Presenter> {

        void getCoinMessageSuccess(HttpCoinMessage obj, String coinName);

        void getCoinMessageFail(Integer code, String toastMessage);

        void getETHMessageSuccess(HttpETHMessage obj, String coinName);

        void getETHMessageFail(Integer code, String toastMessage);

        void getTokenMessageSuccess(Object obj, String contractAddress);

        void getTokenMessageFail(Integer code, String toastMessage);

        void getTokenContractSuccess(Object obj, String ethAddress);

        void getTokenContractFail(Integer code, String toastMessage);

        void getTokenQuerySuccess(Object obj, String ethAddress);

        void getTokenQueryFail(Integer code, String toastMessage);

        void transactionETHRecordSuccess(Object obj);

        void transactionETHRecordFail(Integer code, String toastMessage);

        void getCurrencyDataListSuccess(List<MyCurrencyData> obj);

        void getCurrencyDataListFail(Integer code, String toastMessage);

        void getVersionMessageSuccess(VersionMessage obj);

        void getVersionMessageFail(Integer code, String toastMessage);

        void getNoticeMessageSuccess(List<Notice> notices);

        void getNoticeMessageFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {

        void getCoinMessage(HashMap<String, String> params);

        void getETHMessage(HashMap<String, String> params);

        void getTokenMessage(HashMap<String, String> params);

        void getTokenContract(HashMap<String, String> params);

        void getTokenQuery(HashMap<String, String> params);

        void transactionETHRecord(HashMap<String, String> params);

        void getCurrencyDataList();

        void getVersionMessage();

        void getNoticeMessage(String json);
    }

}
