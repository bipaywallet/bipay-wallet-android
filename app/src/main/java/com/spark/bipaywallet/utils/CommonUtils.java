package com.spark.bipaywallet.utils;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.download.DownloadService;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class CommonUtils {

    /**
     * 将指定内容粘贴到剪贴板
     *
     * @param content 剪切内容
     */
    public static void copyText(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newRawUri("copyLable", Uri.parse(content));
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 获取手机序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serialnum = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
        } catch (Exception ignored) {
        }
        return serialnum;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 程序是否在前台运行
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) MyApplication.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = MyApplication.getApp().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersionName() {
        String version = "";
        try {
            PackageManager packageManager = MyApplication.getApp().getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(MyApplication.getApp().getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (version == null || version.length() <= 0) {
            version = "";
        }
        return version;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return MyApplication.getApp().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return MyApplication.getApp().getResources().getDisplayMetrics().heightPixels;
    }


    /**
     * 检查是否有网络
     */
    public static boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if ((wifiState != null && wifiState == NetworkInfo.State.CONNECTED) || (mobileState != null && mobileState == NetworkInfo.State.CONNECTED)) {
            return true;
        }
        return false;
    }

    /**
     * 更新下载
     *
     * @param url
     */
    public static void showUpDialog(final Context context, final String url, final String filename) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.title(context.getString(R.string.app_name)).btnText(context.getString(R.string.version_cancel), context.getString(R.string.version_ok)).titleTextColor(context.getResources().getColor(R.color.black))
                .btnTextColor(context.getResources().getColor(R.color.dialog_cancel_txt), context.getResources().getColor(R.color.dialog_ok_txt))
                .content(context.getString(R.string.version_title)).contentTextColor(context.getResources().getColor(R.color.font_sec_black)).setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if (StringUtils.isEmpty(url)) {
                            ToastUtils.showToast(context.getString(R.string.version_url_error));
                        } else {
                            Intent intent = new Intent(context, DownloadService.class);
                            intent.putExtra("url", url);
                            intent.putExtra("filename", filename);
                            context.startService(intent);
                            ToastUtils.showToast(context.getString(R.string.version_notify));
                        }
                        dialog.superDismiss();

                    }
                });
        dialog.show();
    }
}
