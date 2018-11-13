package com.spark.bipaywallet.activity.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spark.bipaywallet.data.DataSource;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.factory.UrlFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class MessagePresenter implements MessageContract.Presenter {
    private DataSource dataRepository;
    private MessageContract.View view;

    public MessagePresenter(DataSource dataRepository, MessageContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
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
                view.doPostFail(code, toastMessage);
            }
        });
    }
}
