package com.spark.bipaywallet.activity.kline;


import com.spark.bipaywallet.base.Contract;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface KlineContract {

    interface View extends Contract.BaseView<Presenter> {

        void KDataFail(Integer code, String toastMessage);

        void KDataSuccess(Object obj);

        void getNewKOneDataFail(Integer code, String toastMessage);

        void getNewKOneDataSuccess(Object obj);
    }

    interface Presenter extends Contract.BasePresenter {
        void KData(HashMap<String, String> params);

        void getNewKOneData(HashMap<String, String> params);
    }
}
