package com.spark.bipaywallet.utils;

import android.util.Log;

import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.MyApplication;

/**
 * Created by daiyy on 2018/07/06.
 * 打印工具类
 */
public class LogUtils {
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数


    private static String createLog(String log) {
        getMethodNames(new Throwable().getStackTrace());
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName(); // 方法名
        lineNumber = sElements[1].getLineNumber();// 行数及对应的类名
    }


    public static void e(String message) {
        if (GlobalConstant.isDebug) {
            Log.e(className, createLog(message));
        }
    }


    public static void i(String message) {
        if (GlobalConstant.isDebug) {
            Log.i(className, createLog(message));
        }
    }

    public static void d(String message) {
        if (GlobalConstant.isDebug) {
            Log.d(className, createLog(message));
        }
    }

    public static void v(String message) {
        if (GlobalConstant.isDebug) {
            Log.v(className, createLog(message));
        }
    }

    public static void w(String message) {
        if (GlobalConstant.isDebug) {
            Log.w(className, createLog(message));
        }
    }

    public static void wtf(String message) {
        if (GlobalConstant.isDebug) {
            Log.wtf(className, createLog(message));
        }
    }

    public static void logi(String TAG, String content) {
        if (!MyApplication.getApp().isReleased()) {
            Log.i(TAG, content);
        }
    }

    public static void loge(String TAG, String content) {
        if (!MyApplication.getApp().isReleased()) {
            Log.e(TAG, content);
        }
    }

    public static void logd(String TAG, String content) {
        if (!MyApplication.getApp().isReleased()) {
            Log.d(TAG, content);
        }
    }


}
