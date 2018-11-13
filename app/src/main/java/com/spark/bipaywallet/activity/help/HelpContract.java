package com.spark.bipaywallet.activity.help;

import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.Notice;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class HelpContract {
    interface View extends Contract.BaseView<HelpContract.Presenter> {

        void getHelpMessageSuccess(List<Notice> notices);

        void doPostFail(Integer code, String toastMessage);

    }

    interface Presenter extends Contract.BasePresenter {

        void getHelpMessage(String json);
    }

}
