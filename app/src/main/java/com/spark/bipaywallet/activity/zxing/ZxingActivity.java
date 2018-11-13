package com.spark.bipaywallet.activity.zxing;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.transferpay.TransferPayActivity;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ImageUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 扫码
 */

public class ZxingActivity extends BaseActivity {
    public static final int REQUEST_IMAGE = 3;
    @BindView(R.id.fl_zxing_container)
    FrameLayout fl_zxing_container;

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_STORAGE:
                    chooseFromAlbum();
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
    protected int getActivityLayoutId() {
        return R.layout.activity_zxing;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText(getString(R.string.activity_zxing_photo));
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_zxing_title));
        initCamara();
    }

    private void initCamara() {
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
    }

    @OnClick(R.id.tvGoto)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        checkPermission(GlobalConstant.PERMISSION_STORAGE, Permission.STORAGE);
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    private void chooseFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        ToastUtils.showToast(getString(R.string.zxing_fail));
                        return;
                    }
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            success(result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            failed();
                        }
                    });
                }
                break;
        }
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            success(result);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            failed();
            finish();
        }
    };

    private void success(String result) {
        LogUtils.i("onAnalyzeSuccess");

        if (!StringUtils.isEmpty(result)) {
            String[] strs = result.split(":");

            if (strs.length > 1) {
                if (MyApplication.getApp().getCurrentWallet() != null) {
                    List<MyCoin> coinList = DataSupport.where("walletName = ? and name = ?",
                            MyApplication.getApp().getCurrentWallet().getName(), strs[0]).find(MyCoin.class);

                    if (coinList != null && coinList.size() > 0) {
                        if (coinList.get(0).isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("myCoin", coinList.get(0));
                            bundle.putString("otherAddress", strs[1]);
                            if (strs.length > 2) {
                                bundle.putString("money", strs[2]);
                            }
                            showActivity(TransferPayActivity.class, bundle);
                            finish();
                        } else {
                            ToastUtils.showToast(getString(R.string.zxing_add_coin_first));
                        }
                    } else {
                        ToastUtils.showToast(getString(R.string.zxing_add_coin_first));
                    }
                }
            } else {
                ToastUtils.showToast(getString(R.string.zxing_fail));
            }
        } else {
            ToastUtils.showToast(getString(R.string.zxing_fail));
        }
    }

    private void failed() {
        LogUtils.i("onAnalyzeFailed");
        ToastUtils.showToast(getString(R.string.zxing_fail));
    }

}
