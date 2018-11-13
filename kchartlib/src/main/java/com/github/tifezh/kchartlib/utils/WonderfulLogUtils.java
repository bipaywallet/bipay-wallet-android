package com.github.tifezh.kchartlib.utils;

import android.app.Activity;
import android.util.Log;



/**
 * Created by Administrator on 2017/8/30.
 */

public class WonderfulLogUtils {
    private static String TAG = "com.github.tifezh";


    public static void logi(String content) {
        Log.i(TAG, content);
    }

    public static void loge(String content) {
        Log.e(TAG, content);
    }

    public static void logd(String content) {
        Log.d(TAG, content);
    }


}
