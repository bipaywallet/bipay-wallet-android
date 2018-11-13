package com.spark.bipaywallet.activity.exchangerecord;

import com.spark.bipaywallet.data.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ExchangeRecordPresenter implements ExchangeRecordContract.Presenter {
    private DataSource dataRepository;
    private ExchangeRecordContract.View view;

    public ExchangeRecordPresenter(DataSource dataRepository, ExchangeRecordContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

//    @Override
//    public void getExchangeRecord(String json) {
//        dataRepository.getExchangeCoin(json, new DataSource.DataCallback() {
//            @Override
//            public void onDataLoaded(Object obj) {
//                String reponse = (String) obj;
//                try {
//                    JSONObject object = new JSONObject(reponse);
//                    List<HttpExchangeRecord> list = new Gson().fromJson(object.getJSONArray("result").toString(), new TypeToken<List<HttpExchangeRecord>>() {
//                    }.getType());
//                    view.getExchangeRecordSuccess(list);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onDataNotAvailable(Integer code, String toastMessage) {
//                view.getExchangeRecordFail(code, toastMessage);
//            }
//        });
//    }

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
