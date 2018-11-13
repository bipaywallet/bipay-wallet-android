package com.spark.bipaywallet.activity.exchangedetail;

import com.spark.bipaywallet.base.Contract;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangeDetailContract {
    interface View extends Contract.BaseView<ExchangeDetailContract.Presenter> {
        void getExchangeStatusSuccess(String id, Object obj);

        void getExchangeStatusFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {
        //获取兑换记录
        void getExchangeStatus(String id, String json);
    }

}
