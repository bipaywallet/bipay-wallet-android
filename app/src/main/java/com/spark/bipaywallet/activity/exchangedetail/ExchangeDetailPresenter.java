package com.spark.bipaywallet.activity.exchangedetail;

import com.spark.bipaywallet.data.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangeDetailPresenter implements ExchangeDetailContract.Presenter {
    private DataSource dataRepository;
    private ExchangeDetailContract.View view;

    public ExchangeDetailPresenter(DataSource dataRepository, ExchangeDetailContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void getExchangeStatus(final String id, String json) {
        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    String str = object.getString("result");
                    view.getExchangeStatusSuccess(id, str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.getExchangeStatusFail(code, toastMessage);
            }
        });
    }
}
