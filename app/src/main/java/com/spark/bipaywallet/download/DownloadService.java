package com.spark.bipaywallet.download;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.home.MainActivity;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.utils.FileUtils;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.okhttp.FileCallback;
import com.spark.bipaywallet.utils.okhttp.OkhttpUtils;

import java.io.File;

import okhttp3.Request;

import static com.spark.bipaywallet.app.GlobalConstant.PERMISSION_INSTALL_PACKAGES;

/**
 * Created by 0048104325 on 2017/7/18.
 */

public class DownloadService extends Service {
    private final static String TAG = "DownloadService %s";
    private final static String CHANNELID = "bipay_channel_version_update";
    private String fileDir = "DOWNLOAD";
    private String fileName = "";
    private String url = "";
    private Context mContext;
    private int preProgress = 0;
    private int NOTIFY_ID = 10000;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate");
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("onStartCommand");
        initNotification();
        if (intent != null) {
            fileName = intent.getStringExtra("filename");
            url = intent.getStringExtra("url");
            download(url);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(String url) {
        OkhttpUtils.get().url(url).build().execute(new FileCallback(FileUtils.getLongSaveFile(this, "DOWNLOAD", fileName).getAbsolutePath()) {
            @Override
            public void inProgress(float progress) {
                updateNotification((int) (progress * 100));
            }

            @Override
            public void onError(Request request, Exception e) {
                cancelNotification();
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(File response) {
                installApk(response);
                cancelNotification();
            }
        });
    }

    /**
     * 初始化Notification通知
     */
    public void initNotification() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNELID, getString(R.string.fragment_four_version), NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(mContext)
                    .setChannelId(CHANNELID)
                    .setContentTitle(getString(R.string.fragment_four_version))
                    .setContentText("0%")
                    .setProgress(100, 0, false)
                    .setSmallIcon(R.mipmap.ic_cm_logo);
            notification = builder.build();
        } else {
            builder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(getString(R.string.fragment_four_version))
                    .setContentText("0%")
                    .setSmallIcon(R.mipmap.ic_cm_logo)
                    .setProgress(100, 0, false);
            notification = builder.build();
        }
        notificationManager.notify(NOTIFY_ID, notification);

//        builder = new NotificationCompat.Builder(mContext)
//                .setSmallIcon(R.mipmap.ic_cm_logo)
//                .setContentText("0%")
//                .setContentTitle(getString(R.string.fragment_four_version))
//                .setProgress(100, 0, false);
//        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 更新通知
     */
    public void updateNotification(int progress) {
        int currProgress = progress;
        if (preProgress < currProgress) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, currProgress, false);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
        preProgress = currProgress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFY_ID);
    }

    /**
     * 安装软件
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri apkUri = FileUtils.getUriForFile(mContext, file);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
