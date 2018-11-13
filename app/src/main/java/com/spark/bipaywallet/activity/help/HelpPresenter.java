package com.spark.bipaywallet.activity.help;

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

public class HelpPresenter implements HelpContract.Presenter {
    private DataSource dataRepository;
    private HelpContract.View view;

    public HelpPresenter(DataSource dataRepository, HelpContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public void getHelpMessage(String json) {
        dataRepository.doStringPost(UrlFactory.getHelpUrl(), json, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                String reponse = (String) obj;
                try {
                    JSONObject object = new JSONObject(reponse);
                    if (object.optInt("responseCode") == 200) {
                        List<Notice> notices = new Gson().fromJson(object.getJSONObject("result").getJSONArray("pageList").toString(), new TypeToken<List<Notice>>() {
                        }.getType());
                        view.getHelpMessageSuccess(notices);
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
