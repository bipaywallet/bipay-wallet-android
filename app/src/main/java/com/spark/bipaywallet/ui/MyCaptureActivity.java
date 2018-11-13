package com.spark.bipaywallet.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ImageUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.spark.bipaywallet.activity.zxing.ZxingActivity.REQUEST_IMAGE;

public class MyCaptureActivity extends BaseActivity {
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
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
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
                            ToastUtils.showToast(getString(R.string.zxing_fail));
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
        }

        @Override
        public void onAnalyzeFailed() {
            failed();
        }
    };

    private void success(String result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
        bundle.putString(CodeUtils.RESULT_STRING, result);
        resultIntent.putExtras(bundle);
        MyCaptureActivity.this.setResult(RESULT_OK, resultIntent);
        MyCaptureActivity.this.finish();
    }

    private void failed() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
        bundle.putString(CodeUtils.RESULT_STRING, "");
        resultIntent.putExtras(bundle);
        MyCaptureActivity.this.setResult(RESULT_OK, resultIntent);
        MyCaptureActivity.this.finish();
    }


}
