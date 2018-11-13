package com.spark.bipaywallet.app;

import android.support.annotation.NonNull;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.utils.FileUtils;
import com.spark.bipaywallet.utils.PermissionUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.commonsdk.UMConfigure;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by pc on 2017/3/8.
 */
public class MyApplication extends LitePalApplication {
    public static IWXAPI wxapi;
    private boolean isReleased = false; // 是否发布了
    private static MyApplication app;
    private Wallet currentWallet = null; // 当前钱包
//    private int currentWalletPos = -1; // 侧滑菜单的选中位置
    private boolean isChangeWallet = false; // 是否切换钱包
    private boolean isRecordChangeWalletName = false; // 查询交易记录里是否切换钱包


    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_STORAGE:
                    getCurrentWalletFromFile();
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_STORAGE:
                    ToastUtils.showToast(getString(R.string.storage_permission));
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        LitePal.initialize(this);
        initWechat();
        initUM();
        ZXingLibrary.initDisplayOpinion(this);
        getCurrentWalletFromFileCheck();
    }

    /**
     * 友盟统计初始化
     */
    private void initUM() {
        UMConfigure.init(this, getString(R.string.umeng_id), "bipaywallet", UMConfigure.DEVICE_TYPE_PHONE, null);
    }

    /**
     * 微信分享初始化
     */
    private void initWechat() {
        wxapi = WXAPIFactory.createWXAPI(getApplicationContext(), getString(R.string.app_id), true);
        wxapi.registerApp(getString(R.string.app_id));
    }


    /**
     * 获取程序的Application对象
     */
    public static MyApplication getApp() {
        return app;
    }

    public synchronized void saveCurrentWallet() {
        try {
            File file = FileUtils.getLongSaveFile(this, "Wallet", "wallet.info");
            if (currentWallet == null) {
                if (file.exists()) {
                    file.delete();
                }
                return;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(currentWallet);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentWalletFromFile() {
        try {
            File file = new File(FileUtils.getLongSaveDir(this, "Wallet"), "wallet.info");
            if (file != null && file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                this.currentWallet = (Wallet) ois.readObject();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentWalletFromFileCheck() {
        if (!PermissionUtils.isCanUseStorage(this)) {
            checkPermission(GlobalConstant.PERMISSION_STORAGE, Permission.STORAGE);
        } else {
            getCurrentWalletFromFile();
        }
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    public boolean isReleased() {
        return isReleased;
    }

    public Wallet getCurrentWallet() {
        return currentWallet;
    }

    public synchronized void setCurrentWallet(Wallet currentWallet) {
        this.currentWallet = currentWallet;
        saveCurrentWallet();
    }


//    public int getCurrentWalletPos() {
//        return currentWalletPos;
//    }
//
//    public void setCurrentWalletPos(int currentWalletPos) {
//        this.currentWalletPos = currentWalletPos;
//    }

    public boolean isChangeWallet() {
        return isChangeWallet;
    }

    public void setChangeWallet(boolean changeWallet) {
        isChangeWallet = changeWallet;
    }

    public boolean isRecordChangeWalletName() {
        return isRecordChangeWalletName;
    }

    public void setRecordChangeWalletName(boolean recordChangeWalletName) {
        isRecordChangeWalletName = recordChangeWalletName;
    }
}
