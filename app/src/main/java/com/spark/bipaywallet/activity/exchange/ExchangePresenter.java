package com.spark.bipaywallet.activity.exchange;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangePresenter implements ExchangeContract.Presenter {
    private DataSource dataRepository;
    private ExchangeContract.View view;

    public ExchangePresenter(DataSource dataRepository, ExchangeContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void getExchangeCoin(String json) {
        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    String[] strs = new Gson().fromJson(object.getJSONArray("result").toString(), new TypeToken<String[]>() {
                    }.getType());
                    view.getExchangeCoinSuccess(strs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getExchangeCoinFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getMinAmount(String json) {
        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    String str = object.getString("result");
                    view.getMinAmountSuccess(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getMinAmountFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getExchangeAmount(String json) {
        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    String str = object.getString("result");
                    view.getExchangeAmountSuccess(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getExchangeAmountFail(code, toastMessage);
            }
        });
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



}
