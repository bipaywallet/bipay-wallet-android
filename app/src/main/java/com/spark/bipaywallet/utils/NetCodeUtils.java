package com.spark.bipaywallet.utils;


import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;


/**
 * Created by wonderful on 2017/5/23.
 */

public class NetCodeUtils {

    private static String no_network;
    private static String parse_error;
    private static String time_out;


    static {
        no_network = MyApplication.getApp().getResources().getString(R.string.no_network);
        parse_error = MyApplication.getApp().getResources().getString(R.string.parse_error);
        time_out = MyApplication.getApp().getResources().getString(R.string.time_out);
    }

    public static void checkedErrorCode(BaseActivity activity, Integer code, String toastMessage) {
        String toast = "";
        switch (code) {
            case GlobalConstant.JSON_ERROR:
                toast = parse_error;
                ToastUtils.showToast(toast);
                return;
            case GlobalConstant.OKHTTP_ERROR:
                if (!CommonUtils.checkInternet()) {
                    toast = no_network;
                    ToastUtils.showToast(toast);
                } else if (StringUtils.isNotEmpty(toastMessage) && toastMessage.equals("timeout")) {
                    toast = time_out;
                    ToastUtils.showToast(toast);
                }
                return;
        }
        toast = toastMessage;
        if (!StringUtils.isEmpty(toastMessage)) {
            ToastUtils.showToast(toast);
            return;
        }
    }


}
