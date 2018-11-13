package com.spark.bipaywallet.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 分享工具类
 */
public class ShareUtils {

    //分享图片
    public static boolean shareImage(Activity activity, String title, String text, Bitmap bmp) {
        try {
            if (bmp == null || bmp.isRecycled()) {
                return false;
            }

            File file = null;
            try {
                file = FileUtils.getLongSaveFile(activity, "Image", "bipaywalletshare.jpg");
                if (file.exists()) {
                    file.delete();
                }
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri imageUri = FileUtils.getUriForFile(activity, file);
            if (imageUri == null) return false;

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            if (StringUtils.isNotEmpty(title)) {
                shareIntent.putExtra(Intent.EXTRA_TITLE, title);
            }
            if (StringUtils.isNotEmpty(text)) {
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            }
            activity.startActivity(shareIntent);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //分享文本
    public static boolean shareText(Activity activity, String title, String text) {
        try {
            if (StringUtils.isEmpty(text)) {
                return false;
            }

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            if (StringUtils.isNotEmpty(title)) {
                shareIntent.putExtra(Intent.EXTRA_TITLE, title);
            }
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            activity.startActivity(shareIntent);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //根据应用包名判断是否安装该应用
    public static Boolean isInstalled(Context context, String packageName) {
        boolean bInstalled = false;
        if (packageName == null) return false;
        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            bInstalled = false;
        } else {
            bInstalled = true;
        }

        return bInstalled;
    }


}
