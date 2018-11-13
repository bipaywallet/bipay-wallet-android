package com.spark.bipaywallet.activity.exchangerecord;

import com.spark.bipaywallet.base.Contract;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangeRecordContract {
    interface View extends Contract.BaseView<ExchangeRecordContract.Presenter> {

//        void getExchangeRecordSuccess(Object obj);
//
//        void getExchangeRecordFail(Integer code, String toastMessage);

        void getExchangeStatusSuccess(String id,Object obj);

        void getExchangeStatusFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {
        //获取兑换记录
//        void getExchangeRecord(String json);

        //获取兑换记录
        void getExchangeStatus(String id,String json);
    }

}
