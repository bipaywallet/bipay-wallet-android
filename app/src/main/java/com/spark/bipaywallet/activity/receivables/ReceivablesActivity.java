package com.spark.bipaywallet.activity.receivables;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.SysConfig;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.NumEditTextUtils;
import com.spark.bipaywallet.utils.PermissionUtils;
import com.spark.bipaywallet.utils.ShareUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 收款码
 */

public class ReceivablesActivity extends BaseActivity {
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.llCopy)
    LinearLayout llCopy;
    @BindView(R.id.llSave)
    LinearLayout llSave;
    @BindView(R.id.llMoney)
    LinearLayout llMoney;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.llShare)
    LinearLayout llShare;
    @BindView(R.id.ivQRCode)
    ImageView ivQRCode;
    @BindView(R.id.etMoney)
    EditText etMoney;
    @BindView(R.id.tvCoinName)
    TextView tvCoinName;

    private MyCoin myCoin;
    private Bitmap codeBitmap;
    private Bitmap shareBitmap;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_receivables;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText(getString(R.string.share));
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_receivables_title));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myCoin = (MyCoin) bundle.getSerializable("myCoin");
            if (myCoin != null) {
                tvAddress.setText(myCoin.getAddress());
                if (StringUtils.isNotEmpty(myCoin.getName())) {
                    tvCoinName.setText(myCoin.getName());
                }
            }
        }
        ivQRCode.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isEmpty(myCoin.getAddress()))
                    return;
                codeBitmap = createQRCode(myCoin.getName() + ":" + myCoin.getAddress() + ":0", Math.min(ivQRCode.getWidth(), ivQRCode.getHeight()));
                ivQRCode.setImageBitmap(codeBitmap);
            }
        });
    }

    @OnClick({R.id.tvGoto, R.id.llCopy, R.id.llSave})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llSave:
                doSave();
                break;
            case R.id.tvGoto:
                shareBitmap = getBitmap(llShare);
                if (shareBitmap != null) {
                    ShareUtils.shareImage(this, "", "", shareBitmap);
                }
                break;
            case R.id.llCopy:
                CommonUtils.copyText(ReceivablesActivity.this, tvAddress.getText().toString());
                ToastUtils.showToast(R.string.copy_success);
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        etMoney.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (NumEditTextUtils.isVaildText(etMoney)) {
                    ivQRCode.post(new Runnable() {
                        @Override
                        public void run() {
                            if (StringUtils.isEmpty(myCoin.getAddress()))
                                return;
                            codeBitmap = createQRCode(myCoin.getName() + ":" + myCoin.getAddress() + ":" + etMoney.getText().toString(),
                                    Math.min(ivQRCode.getWidth(), ivQRCode.getHeight()));
                            ivQRCode.setImageBitmap(codeBitmap);

                            Log.i("sx", myCoin.getName() + ":" + myCoin.getAddress() + ":" + etMoney.getText().toString());
                        }
                    });
                }
            }
        });
    }

    private void doSave() {
        if (!PermissionUtils.isCanUseStorage(ReceivablesActivity.this))
            checkPermission(GlobalConstant.PERMISSION_STORAGE, Permission.STORAGE);
        else try {
            shareBitmap = getBitmap(llShare);
            save(shareBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void showShareDialog() {
//        final ShareDialog shareDialog = new ShareDialog(this);
//        shareDialog.show();
//        shareDialog.setClicklistener(new ShareDialog.ClickListenerInterface() {
//            @Override
//            public void doWeixin() {
//                shareDialog.dismiss();
//                shareToWeixin(saveBitmap, true);
//            }
//
//            @Override
//            public void doPyq() {
//                shareDialog.dismiss();
//                shareToWeixin(saveBitmap, false);
//            }
//
//            @Override
//            public void doCancel() {
//                shareDialog.dismiss();
//            }
//
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (shareBitmap != null && !shareBitmap.isRecycled()) {
            shareBitmap.recycle();
            shareBitmap = null;
        }
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(ReceivablesActivity.this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_STORAGE:
                    try {
                        shareBitmap = getBitmap(llShare);
                        save(shareBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 2);   //设置白边大小 取值为 0- 4 越大白边越大
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void save(Bitmap saveBitmap) {
        if (saveBitmap == null || saveBitmap.isRecycled()) return;

        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());

        File folderFile = new File(SysConfig.myDir);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File file = new File(SysConfig.myDir + fileName + ".jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = Uri.fromFile(file);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        ToastUtils.showToast(getString(R.string.save_success));
    }

//    private void shareToWeixin(final Bitmap saveBitmap, final boolean isToWeixin) {
//        if (!isInstalled(this, "com.tencent.mm")) {
//            ToastUtils.showToast(getString(R.string.installed_error));
//            return;
//        }
//
//        if (saveBitmap == null || saveBitmap.isRecycled()) return;
//
//        WXImageObject imageObject = new WXImageObject(saveBitmap);
//        final WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = imageObject;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap thumbBmp = Bitmap.createScaledBitmap(saveBitmap, 120, 120, true);
//                msg.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
//
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = buildTransaction("img");
//                req.message = msg;
//                // 设置分享渠道
//                req.scene = isToWeixin ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
//
//                wxapi.sendReq(req);
//            }
//        }).start();
//    }

//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }

    /**
     * @param view 需要截取图片的view
     * @return 截图
     */
    private Bitmap getBitmap(View view) {
        if (view == null) {
            return null;
        }

        llMoney.setVisibility(View.GONE);
        tvMoney.setVisibility(View.GONE);

        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenshot);
        view.draw(canvas);

        llMoney.setVisibility(View.VISIBLE);
        tvMoney.setVisibility(View.VISIBLE);

        return screenshot;
    }


}
