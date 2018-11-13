package com.spark.bipaywallet.activity.exchange;

import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangeContract {
    interface View extends Contract.BaseView<ExchangeContract.Presenter> {

        void getExchangeCoinSuccess(Object obj);

        void getExchangeCoinFail(Integer code, String toastMessage);

        void getMinAmountSuccess(Object obj);

        void getMinAmountFail(Integer code, String toastMessage);

        void getExchangeAmountSuccess(Object obj);

        void getExchangeAmountFail(Integer code, String toastMessage);

        void getCoinMessageSuccess(HttpCoinMessage obj, String coinName);

        void getCoinMessageFail(Integer code, String toastMessage);

        void getETHMessageSuccess(HttpETHMessage obj, String coinName);

        void getETHMessageFail(Integer code, String toastMessage);

    }

    interface Presenter extends Contract.BasePresenter {
        //获取兑换支持的币种
        void getExchangeCoin(String json);

        //获取需要发送的最小量
        void getMinAmount(String json);

        //使用交换而获得的估计硬币数量,汇率
        void getExchangeAmount(String json);

        void getCoinMessage(HashMap<String, String> params);

        void getETHMessage(HashMap<String, String> params);
    }

}
