package com.spark.bipaywallet.activity.home;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCurrencyData;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.entity.VersionMessage;
import com.spark.bipaywallet.factory.UrlFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public class MainPresenter implements MainContract.Presenter {
    private DataSource dataRepository;
    private MainContract.View view;

    public MainPresenter(DataSource dataRepository, MainContract.View view) {
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
    public void getTokenContract(HashMap<String, String> params) {
        dataRepository.getTokenContract(params, new DataSource.CoinDataCallback() {
            @Override
            public void onDataLoaded(Object obj, String ethAddress) {
                view.getTokenContractSuccess(obj, ethAddress);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getTokenContractFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getTokenQuery(HashMap<String, String> params) {
        dataRepository.getTokenQuery(params, new DataSource.CoinDataCallback() {
            @Override
            public void onDataLoaded(Object obj, String ethAddress) {
                view.getTokenQuerySuccess(obj, ethAddress);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getTokenQueryFail(code, toastMessage);
            }
        });
    }

    @Override
    public void transactionETHRecord(HashMap<String, String> params) {
        dataRepository.transactionETHRecord(params, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.transactionETHRecordSuccess(obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.transactionETHRecordFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getCurrencyDataList() {
        dataRepository.getCurrencyDataList(new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.getCurrencyDataListSuccess((List<MyCurrencyData>) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getCurrencyDataListFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getVersionMessage() {
        dataRepository.getVersionMessage(new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.getVersionMessageSuccess((VersionMessage) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getVersionMessageFail(code, toastMessage);
            }
        });
    }

    @Override
    public void getNoticeMessage(String json) {
        dataRepository.doStringPost(UrlFactory.getNoticeUrl(), json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    if (object.optInt("responseCode") == 200) {
                        List<Notice> notices = new Gson().fromJson(object.getJSONObject("result").getJSONArray("pageList").toString(), new TypeToken<List<Notice>>() {
                        }.getType());
                        view.getNoticeMessageSuccess(notices);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getNoticeMessageFail(code, toastMessage);
            }
        });
    }
}
