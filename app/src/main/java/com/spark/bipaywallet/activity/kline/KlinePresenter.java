package com.spark.bipaywallet.activity.kline;


import com.spark.bipaywallet.data.DataSource;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/17.
 */

public class KlinePresenter implements KlineContract.Presenter {
    private DataSource dataRepository;
    private KlineContract.View view;

    public KlinePresenter(DataSource dataRepository, KlineContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void KData(HashMap<String, String> params) {
        dataRepository.KData(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.KDataSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.KDataFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getNewKOneData(HashMap<String, String> params) {
        dataRepository.getNewKOneData(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.getNewKOneDataSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getNewKOneDataFail(code, toastMessage);
            }
        });
    }
}
