package com.spark.bipaywallet.activity.message;

import com.spark.bipaywallet.base.Contract;
import com.spark.bipaywallet.entity.Notice;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class MessageContract {
    interface View extends Contract.BaseView<MessageContract.Presenter> {

        void getNoticeMessageSuccess(List<Notice> notices);

        void doPostFail(Integer code, String toastMessage);

    }

    interface Presenter extends Contract.BasePresenter {

        void getNoticeMessage(String json);
    }

}
