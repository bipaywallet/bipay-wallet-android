package com.spark.bipaywallet.activity.transferpay;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.showlinkman.ShowLinkManActivity;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.dialog.ConfirmPayDialog;
import com.spark.bipaywallet.dialog.PasswordDialog;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.ui.MyCaptureActivity;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.NumEditTextUtils;
import com.spark.bipaywallet.utils.PermissionUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 转账
 */

public class TransferPayActivity extends BaseActivity implements TransferPayContract.View {
    public static final int SELECT_LINKMAN = 1;
    public static final int REQUEST_CODE = 2;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etRemark)
    EditText etRemark;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvCoinName)
    TextView tvCoinName;
    @BindView(R.id.ivAddressScan)
    ImageView ivSys;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tvSeekPro)
    TextView tvSeekPro;//当前手续费
    @BindView(R.id.etMoney)
    EditText etMoney;

    private String currFee = "0";
    private BigInteger defaultPro;
    private MyCoin myCoin;
    private String otherAddress;
    private TransferPayContract.Presenter presenter;
    private List<HttpCoinMessage.Utxo> utxoList;
    private String totalAmount;
    private int nonce;
    private String tokenTotalAmount;
    private String usdtTotalAmount;
    private int tokenNonce;

    private AES mAes;

    private java.text.NumberFormat NF = java.text.NumberFormat.getInstance();
    private SimpleDateFormat timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    startSys();
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    ToastUtils.showToast(getString(R.string.camera_permission));
                    break;
            }
        }
    };

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_transferpay;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText(getString(R.string.fragment_four_man));
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_trade_details_zhuanzhang));
        new TransferPayPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        NF.setGroupingUsed(false);//去掉科学计数法显示
        tvSeekPro.setText("0");
        jniUtil = JNIUtil.getInstance();
        mAes = new AES();
        utxoList = new ArrayList<>();
    }

    @Override
    protected void obtainData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myCoin = (MyCoin) bundle.getSerializable("myCoin");
            otherAddress = bundle.getString("otherAddress");
            String bundleMoney = bundle.getString("money");
            if (myCoin != null) {
                setTitle(myCoin.getName() + " " + getString(R.string.activity_transfer_pay_title));
            }
            if (!StringUtils.isEmpty(otherAddress)) {
                etAddress.setText(otherAddress);
            }
            if (!StringUtils.isEmpty(bundleMoney) && MathUtils.getBigDecimalCompareTo(bundleMoney, "0", 18) > 0) {
                etMoney.setText(bundleMoney);
            }
        }

    }

    @OnClick({R.id.ivAddressScan, R.id.tvGoto, R.id.tvNext})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.ivAddressScan:
                if (!PermissionUtils.isCanUseCamera(TransferPayActivity.this))
                    checkPermission(GlobalConstant.PERMISSION_CAMERA, Permission.CAMERA);
                else startSys();
                break;
            case R.id.tvGoto:
                Intent intent = new Intent(TransferPayActivity.this, ShowLinkManActivity.class);
                startActivityForResult(intent, SELECT_LINKMAN);
                break;
            case R.id.tvNext:
                doNext();
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        etMoney.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                NumEditTextUtils.isVaildText(etMoney);
            }
        });
    }

    private void doNext() {
        String moneyStr = etMoney.getText().toString();
        String addressStr = etAddress.getText().toString();
        if (StringUtils.isEmpty(moneyStr, addressStr)) {
            ToastUtils.showToast(R.string.Incomplete_information);
            return;
        }
        LogUtils.i(addressStr + "***add***" + myCoin.getCoinType());

        boolean isVerify = jniUtil.verifyCoinAddress(addressStr, myCoin.getCoinType());
        if (!isVerify) {
            ToastUtils.showToast(R.string.address_not_verify);
            return;
        }

        if (addressStr.equals(myCoin.getAddress())) {
            ToastUtils.showToast(R.string.address_not_allow);
            return;
        }

        if (MathUtils.getBigDecimalCompareTo(moneyStr, "0", 18) <= 0) {
            ToastUtils.showToast(R.string.transfer_pay_warn);
            return;
        }
        if (MathUtils.getBigDecimalCompareTo(currFee, "0", 18) <= 0) {
            ToastUtils.showToast(R.string.fee_warn);
            return;
        }
        if (CommonUtils.checkInternet()) {
            if (myCoin != null) {
                if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 ||
                        myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", myCoin.getAddress());
                    params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getCoinMessage(params);
                } else if (myCoin.getCoinType() == 207) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", myCoin.getAddress());
                    params.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getCoinMessage(params);

                    HashMap<String, String> usdtParams = new HashMap<>();
                    usdtParams.put("address", myCoin.getAddress());
                    usdtParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getCoinMessage(usdtParams);
                } else if (myCoin.getCoinType() == 60) {
                    HashMap<String, String> ethParams = new HashMap<>();
                    ethParams.put("address", myCoin.getAddress());
                    ethParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    if (myCoin.getName().equals("ETH")) {
                        presenter.getETHMessage(ethParams);
                    } else {
                        presenter.getETHMessage(ethParams);
                        HashMap<String, String> tokenParams = new HashMap<>();
                        tokenParams.put("address", myCoin.getAddress());
                        tokenParams.put("contractAddress", myCoin.getContractAddress());
                        tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                        presenter.getTokenMessage(tokenParams);
                    }
                }
            }
        } else {
            ToastUtils.showToast(getString(R.string.no_network));
            return;
        }

        compareMoney(moneyStr, addressStr);
    }

    private void compareMoney(String money, String address) {
        String pay = "0";
        if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 ||
                myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
            pay = MathUtils.getBigDecimalAdd(money, MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), 8);
        } else if (myCoin.getCoinType() == 207) {
            if (MathUtils.getBigDecimalCompareTo(usdtTotalAmount, money, 18) <= -1
                    || MathUtils.getBigDecimalCompareTo(usdtTotalAmount, "0", 18) == 0) {
                ToastUtils.showToast(R.string.money_not_enough);
                return;
            }
            pay = MathUtils.getBigDecimalAdd(MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), "0.00001092", 8);
        } else if (myCoin.getCoinType() == 60) {
            if (myCoin.getName().equals("ETH")) {
                pay = MathUtils.getBigDecimalAdd(money, tvSeekPro.getText().toString(), 18);
            } else {
                if (MathUtils.getBigDecimalCompareTo(tokenTotalAmount, money, 18) <= -1
                        || MathUtils.getBigDecimalCompareTo(tokenTotalAmount, "0", 18) == 0) {
                    ToastUtils.showToast(R.string.money_not_enough);
                    return;
                }
                pay = MathUtils.getBigDecimalRundNumber(tvSeekPro.getText().toString(), 18);

                if (MathUtils.getBigDecimalCompareTo(totalAmount, pay, 18) <= -1
                        || MathUtils.getBigDecimalCompareTo(totalAmount, "0", 18) == 0) {
                    ToastUtils.showToast(R.string.fee_not_enough);
                    return;
                }
            }
        }
        Log.i("sx", pay + "---pay");
        if (MathUtils.getBigDecimalCompareTo(totalAmount, pay, 18) <= -1
                || MathUtils.getBigDecimalCompareTo(totalAmount, "0", 18) == 0) {
            if (myCoin.getCoinType() == 207) {
                ToastUtils.showToast(R.string.fee_not_enough);
            } else {
                ToastUtils.showToast(R.string.money_not_enough);
            }
            return;
        }

        showConfirmPayDialog(money, address, tvSeekPro.getText().toString(), etRemark.getText().toString());
    }

    private void showConfirmPayDialog(final String money, String address, String fee, String remark) {
        final ConfirmPayDialog confirmPayDialog = new ConfirmPayDialog(this, money + " " + myCoin.getName(),
                address,
                fee + " " + tvCoinName.getText().toString(),
                remark);
        confirmPayDialog.show();
        confirmPayDialog.setClicklistener(new ConfirmPayDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmPayDialog.dismiss();
                showPasswordDialog(money);
            }

            @Override
            public void doCancel() {
                confirmPayDialog.dismiss();
            }
        });
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    private void startSys() {
        Intent intent = new Intent(TransferPayActivity.this, MyCaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void showPasswordDialog(final String money) {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                if (mAes != null && MyApplication.getApp().getCurrentWallet() != null) {
                    String deString = mAes.decrypt(MyApplication.getApp().getCurrentWallet().getEncrypMasterKey(), password);

                    if (deString != null) {
                        passwordDialog.dismiss();

                        if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 ||
                                myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208) {
                            confirmDVC(money, password);
                        } else if (myCoin.getCoinType() == 145) {
                            confirmBCH(money, password);
                        } else if (myCoin.getCoinType() == 207) {
                            confirmUSDT(money, password);
                        } else if (myCoin.getCoinType() == 60) {
                            if (myCoin.getName().equals("ETH")) {
                                confirmETH(money, password);
                            } else {
                                confirmToken(money, password);
                            }
                        }
                    } else {
                        ToastUtils.showToast(getString(R.string.password_confirm_error));
                    }
                }
            }

            @Override
            public void doCancel() {
                passwordDialog.dismiss();
            }
        });
    }

    private void confirmUSDT(String money, String password) {
        String pay = MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8);
        String enough = MathUtils.getBigDecimalAdd(pay, "0.00001092", 8);
        String currMoney = "0.00";
        int i = 0;

        do {
            currMoney = MathUtils.getBigDecimalAdd(currMoney,
                    MathUtils.getBigDecimalDivide(utxoList.get(i).getAmount(), MathUtils.getBigDecimal10Pow8(), 8),
                    8);
            i++;
        } while (MathUtils.getBigDecimalCompareTo(currMoney, enough, 18) <= -1);

        JSONObject jsonObject = getUSDTJSONObject(i, money, pay, currMoney);
        Log.i("sx", jsonObject.toString());

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), myCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            StringBuffer stringBuffer = new StringBuffer();
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, 0, CoinTypeEnum.BTC.getKeyPrefix());
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
                stringBuffer.append(signPrivk);
                for (int k = 0; k < i - 1; k++) {
                    stringBuffer.append(" " + signPrivk);
                }
            }

            Log.i("sx", stringBuffer.toString() + "---stringBuffer");

            if (!StringUtils.isEmpty(newTransaction, stringBuffer.toString())) {
                String signature = jniUtil.signatureForTransfer(newTransaction, stringBuffer.toString(), 0);
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPay(params);
            }
        }
    }

    private void confirmToken(String money, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", myCoin.getAddress());
            jsonObject.put("to", etAddress.getText().toString());
            String v = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(money, MathUtils.getBigDecimalPow("10", myCoin.getDecimals(), 18), 18), 0);
            jsonObject.put("value", v);
            jsonObject.put("nonce", tokenNonce + "");
            String g = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("gasprice", g);
            jsonObject.put("gas", "60000");
            jsonObject.put("contractAddr", myCoin.getContractAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sx", jsonObject.toString());

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), myCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            Log.i("sx", newTransaction);
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            Log.i("sx", deSubKey + "sub");
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, myCoin.getCoinType(), -1);
            if (!StringUtils.isEmpty(signPrivk)) {
                Log.i("sx", signPrivk + "signPrivk");
                jniUtil.freeAlloc(signPrivk);
            }

            if (!StringUtils.isEmpty(newTransaction, signPrivk)) {
                String signature = jniUtil.signatureForTransfer(newTransaction, signPrivk, myCoin.getCoinType());
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPayETH(params);
            }
        }
    }

    private void confirmETH(String money, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", myCoin.getAddress());
            jsonObject.put("to", etAddress.getText().toString());
            String v = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(money, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("value", v + "");
            jsonObject.put("nonce", nonce + "");
            String g = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("gasprice", g + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sx", jsonObject.toString());

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), myCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, myCoin.getCoinType(), -1);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
            }

            if (!StringUtils.isEmpty(newTransaction, signPrivk)) {
                String signature = jniUtil.signatureForTransfer(newTransaction, signPrivk, myCoin.getCoinType());
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPayETH(params);
            }
        }
    }

    private void confirmDVC(String money, String password) {
        String pay = MathUtils.getBigDecimalAdd(money, MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), 8);
        String currMoney = "0.00";
        int i = 0;

        do {
            currMoney = MathUtils.getBigDecimalAdd(currMoney, MathUtils.getBigDecimalDivide(utxoList.get(i).getAmount(), MathUtils.getBigDecimal10Pow8(), 8), 8);
            i++;
        } while (MathUtils.getBigDecimalCompareTo(currMoney, pay, 18) <= -1);

        JSONObject jsonObject = getDVCJSONObject(i, money, pay, currMoney);
        Log.i("sx", jsonObject.toString() + "---json");

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), myCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            StringBuffer stringBuffer = new StringBuffer();
            CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(myCoin.getName());
            int keyPrefix = -1;
            if (coinTypeEnum != null) {
                keyPrefix = coinTypeEnum.getKeyPrefix();
            }
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, myCoin.getCoinType(), keyPrefix);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
                stringBuffer.append(signPrivk);
                for (int k = 0; k < i - 1; k++) {
                    stringBuffer.append(" " + signPrivk);
                }
            }

            Log.i("sx", stringBuffer.toString() + "---stringBuffer");

            if (!StringUtils.isEmpty(newTransaction, stringBuffer.toString())) {
                String signature = jniUtil.signatureForTransfer(newTransaction, stringBuffer.toString(), myCoin.getCoinType());
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPay(params);
            }
        }
    }

    private void confirmBCH(String money, String password) {
        String pay = MathUtils.getBigDecimalAdd(money, MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), 8);
        String currMoney = "0.00";
        int i = 0;

        do {
            currMoney = MathUtils.getBigDecimalAdd(currMoney, MathUtils.getBigDecimalDivide(utxoList.get(i).getAmount(), MathUtils.getBigDecimal10Pow8(), 8), 8);
            i++;
        } while (MathUtils.getBigDecimalCompareTo(currMoney, pay, 18) <= -1);

        JSONObject jsonObject = getDVCJSONObject(i, money, pay, currMoney);
        Log.i("sx", jsonObject.toString() + "---json");

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), myCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            StringBuffer stringBuffer = new StringBuffer();
            CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(myCoin.getName());
            int keyPrefix = -1;
            if (coinTypeEnum != null) {
                keyPrefix = coinTypeEnum.getKeyPrefix();
            }
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, myCoin.getCoinType(), keyPrefix);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
                stringBuffer.append(signPrivk);
                for (int k = 0; k < i - 1; k++) {
                    stringBuffer.append(" " + signPrivk);
                }
            }

            Log.i("sx", stringBuffer.toString() + "---stringBuffer");

            if (!StringUtils.isEmpty(newTransaction, stringBuffer.toString())) {
                String signature = jniUtil.SignSignatureForTransfer(newTransaction, stringBuffer.toString(), myCoin.getCoinType(), getBCHReserved(i));
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPay(params);
            }
        }
    }

    @Override
    protected void loadData() {
        if (myCoin != null) {
            if (myCoin.getCoinType() == 0) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", myCoin.getAddress());
                params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                tvCoinName.setText(" BTC/KB");
                presenter.getCoinMessage(params);
                initSeekBar("0.00001");

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (myCoin.getCoinType() == 207) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", myCoin.getAddress());
                params.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(params);

                HashMap<String, String> usdtParams = new HashMap<>();
                usdtParams.put("address", myCoin.getAddress());
                usdtParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(usdtParams);

                tvCoinName.setText(" BTC/KB");
                initSeekBar("0.00001");

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (myCoin.getCoinType() == 206 ||
                    myCoin.getCoinType() == 208 || myCoin.getCoinType() == 2 || myCoin.getCoinType() == 145) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", myCoin.getAddress());
                params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(params);

                if (myCoin.getCoinType() == 206 || myCoin.getCoinType() == 208) {
                    tvCoinName.setText(" " + myCoin.getName());
                    initSeekBar("0.008");
                } else if (myCoin.getCoinType() == 2) {
                    tvCoinName.setText(" " + myCoin.getName() + "/KB");
                    initSeekBar("0.001");
                } else if (myCoin.getCoinType() == 145) {
                    tvCoinName.setText(" " + myCoin.getName() + "/KB");
                    initSeekBar("0.00001");
                }

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (myCoin.getCoinType() == 60) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", myCoin.getAddress());
                params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                tvCoinName.setText(" ETH");
                if (myCoin.getName().equals("ETH")) {
                    presenter.getETHMessage(params);
                } else {
                    presenter.getETHMessage(params);
                    HashMap<String, String> tokenParams = new HashMap<>();
                    tokenParams.put("address", myCoin.getAddress());
                    tokenParams.put("contractAddress", myCoin.getContractAddress());
                    tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getTokenMessage(tokenParams);
                }
                initSeekBar("3");

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", "ETH");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_LINKMAN:
                if (data != null) {
                    String addressStr = data.getStringExtra("addressStr");
                    if (!StringUtils.isEmpty(addressStr)) {
                        Log.i("sx", addressStr + "--ok");
                        etAddress.setText(addressStr);
                    }
                }
                break;
            case REQUEST_CODE:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);

                        if (!StringUtils.isEmpty(result)) {
                            String[] strs = result.split(":");

                            if (strs.length > 1) {
                                if (myCoin != null && strs[0].equalsIgnoreCase(myCoin.getName())) {
                                    etAddress.setText(strs[1]);

                                    if (strs.length > 2) {
                                        if (!StringUtils.isEmpty(strs[2]) && MathUtils.getBigDecimalCompareTo(strs[2], "0", 18) > 0) {
                                            etMoney.setText(strs[2]);
                                        }
                                    }
                                } else {
//                                    ToastUtils.showToast(getString(R.string.zxing_coin_error));
                                    etAddress.setText(result);
                                }
                            } else {
                                etAddress.setText(result);
                            }
                        } else {
                            ToastUtils.showToast(getString(R.string.zxing_fail));
                        }
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        ToastUtils.showToast(getString(R.string.zxing_fail));
                    }
                }
                break;
        }
    }

    @Override
    public void setPresenter(TransferPayContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getCoinMessageSuccess(HttpCoinMessage obj, String coinName) {
        if (obj == null) return;

        Log.i("sx", obj.getAddress() + "---tfp");
        if (coinName.equalsIgnoreCase("USDT")) {
            usdtTotalAmount = MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8);
        } else {
            totalAmount = MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8);
        }

        if (obj.getUtxo() != null && obj.getUtxo().size() > 0) {
            utxoList.clear();
            utxoList.addAll(obj.getUtxo());
        }
    }

    @Override
    public void getCoinMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getETHMessageSuccess(HttpETHMessage obj, String coinName) {
        if (obj == null) return;

        totalAmount = MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8);
        nonce = obj.getNonce();

        Log.i("sx", totalAmount + "---totalAmount---" + nonce + "---nonce");
    }

    @Override
    public void getETHMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getTokenMessageSuccess(Object obj, String contractAddress) {
        if (obj == null) return;

        HttpETHMessage message = (HttpETHMessage) obj;
        tokenTotalAmount = MathUtils.getBigDecimalDivide(message.getTotalAmount(),
                MathUtils.getBigDecimalPow("10", myCoin.getDecimals(), 8), 8);
        tokenNonce = message.getNonce();
    }

    @Override
    public void getTokenMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    private void saveRecordBean(String txid) {
        MyRecordBean myRecordBean = new MyRecordBean();
        myRecordBean.setCoinName(myCoin.getName());
        myRecordBean.setAddress(myCoin.getAddress());
        myRecordBean.setTxid(txid);
        myRecordBean.setBlockHeight(0);
        myRecordBean.setTime(timeDF.format(new Date()));

        Log.i("sx", timeDF.format(new Date()));

        if (myCoin.getCoinType() == 60) {
            if (myCoin.getName().equals("ETH")) {
                myRecordBean.setFee(MathUtils.getBigDecimalMultiply(currFee, "21000", 18));
            } else {
                myRecordBean.setFee(MathUtils.getBigDecimalMultiply(currFee, "60000", 18));
                myRecordBean.setContractAddress(myCoin.getContractAddress());
            }
        } else {
            myRecordBean.setFee(currFee);
        }

        myRecordBean.setValue(MathUtils.getBigDecimalRundNumber(etMoney.getText().toString(), 8));
        myRecordBean.setFrom(myCoin.getAddress());
        myRecordBean.setTo(etAddress.getText().toString());
        myRecordBean.setOut(true);

        if (!StringUtils.isEmpty(etRemark.getText().toString())) {
            myRecordBean.setRemark(etRemark.getText().toString());
        }

        //保存交易记录
        myRecordBean.save();
    }

    @Override
    public void transferPaySuccess(Object obj) {
        if (obj != null) {
            Log.i("sx", (String) obj);
            saveRecordBean((String) obj);
            showActivity(TransferPaySuccessActivity.class, null);
            finish();
        }
    }

    @Override
    public void transferPayFail(Integer code, String toastMessage) {
        if (code == -1) {
            ToastUtils.showToast(getString(R.string.trade_underway));
        } else {
            NetCodeUtils.checkedErrorCode(this, code, toastMessage);
        }
    }

    @Override
    public void transferPayETHSuccess(Object obj) {
        if (obj != null) {
            Log.i("sx", (String) obj);
            saveRecordBean((String) obj);
            showActivity(TransferPaySuccessActivity.class, null);
            finish();
        }
    }

    @Override
    public void transferPayETHFail(Integer code, String toastMessage) {
        if (code == -32000) {
            ToastUtils.showToast(getString(R.string.trade_underway));
        } else {
            NetCodeUtils.checkedErrorCode(this, code, toastMessage);
        }
    }

    @Override
    public void getServiceChargeSuccess(Object obj) {
        Log.i("sx", "succ");

        if (obj == null) return;

        initSeekBar((String) obj);
    }

    @Override
    public void getServiceChargeFail(Integer code, String toastMessage) {

    }

    private void initSeekBar(final String fee) {
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 || myCoin.getCoinType() == 207 ||
                            myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                        currFee = MathUtils.getBigDecimalAdd(fee,
                                MathUtils.getBigDecimalDivide(String.valueOf(progress), MathUtils.getBigDecimal10Pow8(), 8),
                                8);
                        tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(currFee, 8));
                    } else if (myCoin.getCoinType() == 60) {
                        currFee = MathUtils.getBigDecimalDivide(MathUtils.getBigDecimalAdd(fee, MathUtils.getBigDecimalDivide(String.valueOf(progress), "100", 18), 18),
                                MathUtils.getBigDecimalPow("10", 9, 18),
                                18);

                        if (myCoin.getName().equals("ETH")) {
                            tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "21000", 18), 8));
                        } else {
                            tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "60000", 18), 8));
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 || myCoin.getCoinType() == 207 ||
                    myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                String minfee = MathUtils.getBigDecimalMultiply(fee, "1", 8);
                String maxfee = MathUtils.getBigDecimalMultiply(fee, "10", 8);

                BigDecimal bd = new BigDecimal(MathUtils.getBigDecimalSubtract(MathUtils.getBigDecimalMultiply(maxfee, MathUtils.getBigDecimal10Pow8(), 8),
                        MathUtils.getBigDecimalMultiply(minfee, MathUtils.getBigDecimal10Pow8(), 8),
                        8))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                BigInteger proMax = new BigInteger(bd.toString());
                seekBar.setMax(proMax.intValue());


                BigDecimal bd2 = new BigDecimal(MathUtils.getBigDecimalSubtract(MathUtils.getBigDecimalMultiply(MathUtils.getBigDecimalMultiply(fee, "2", 8), MathUtils.getBigDecimal10Pow8(), 8),
                        MathUtils.getBigDecimalMultiply(minfee, MathUtils.getBigDecimal10Pow8(), 8),
                        8))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                defaultPro = new BigInteger(bd2.toString());
                seekBar.setProgress(defaultPro.intValue());

                Log.i("sx", minfee + "--" + maxfee + "--" + proMax + "--" + defaultPro + "dpdpdp");

                currFee = MathUtils.getBigDecimalMultiply(fee, "2", 8);
                tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(currFee, 8));
            } else if (myCoin.getCoinType() == 60) {
                String minfee = MathUtils.getBigDecimalMultiply(fee, "1", 8);
                String maxfee = MathUtils.getBigDecimalMultiply(fee, "10", 8);

                BigDecimal bd = new BigDecimal(MathUtils.getBigDecimalSubtract(MathUtils.getBigDecimalMultiply(maxfee, "100", 8),
                        MathUtils.getBigDecimalMultiply(minfee, "100", 8),
                        8))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                BigInteger proMax = new BigInteger(bd.toString());
                seekBar.setMax(proMax.intValue());

                BigDecimal bd2 = new BigDecimal(MathUtils.getBigDecimalSubtract(MathUtils.getBigDecimalMultiply(MathUtils.getBigDecimalMultiply(fee, "2", 8), "100", 8),
                        MathUtils.getBigDecimalMultiply(minfee, "100", 8),
                        8))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                defaultPro = new BigInteger(bd2.toString());
                seekBar.setProgress(defaultPro.intValue());

                currFee = MathUtils.getBigDecimalMultiply(MathUtils.getBigDecimalDivide(fee, MathUtils.getBigDecimalPow("10", 9, 18), 18),
                        "2",
                        18);
                if (myCoin.getName().equals("ETH")) {
                    tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "21000", 18), 8));
                } else {
                    tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "60000", 18), 8));
                }
            }
        }
    }

    //DVC
    private JSONObject getDVCJSONObject(int i, String money, String pay, String currMoney) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("inputs_count", i);
            JSONArray inja = new JSONArray();
            for (int j = 0; j < i; j++) {
                JSONObject jo = new JSONObject();
                jo.put("prev_position", Integer.valueOf(utxoList.get(j).getIndexNo()));
                jo.put("prev_tx_hash", utxoList.get(j).getTxid());
                inja.put(jo);
            }
            jsonObject.put("inputs", inja);

            JSONArray outja = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put("address", etAddress.getText().toString());
            jo.put("value", MathUtils.getBigDecimalRundNumber(money, 8));
            outja.put(jo);

            if (MathUtils.getBigDecimalCompareTo(pay, currMoney, 18) != 0) {
                jsonObject.put("outputs_count", 2);

                JSONObject jo2 = new JSONObject();
                jo2.put("address", myCoin.getAddress());
                jo2.put("value", MathUtils.getBigDecimalSubtract(currMoney, pay, 8));
                outja.put(jo2);
            } else {
                jsonObject.put("outputs_count", 1);
            }

            jsonObject.put("outputs", outja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    //USDT
    private JSONObject getUSDTJSONObject(int i, String money, String pay, String currMoney) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("inputs_count", i);
            JSONArray inja = new JSONArray();
            for (int j = 0; j < i; j++) {
                JSONObject jo = new JSONObject();
                jo.put("prev_position", Integer.valueOf(utxoList.get(j).getIndexNo()));
                jo.put("prev_tx_hash", utxoList.get(j).getTxid());
                inja.put(jo);
            }
            jsonObject.put("inputs", inja);

            JSONArray outja = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put("address", etAddress.getText().toString());
            jo.put("value", MathUtils.getBigDecimalRundNumber("0.00000546", 8));
            outja.put(jo);

            if (pay != currMoney) {
                jsonObject.put("outputs_count", 2);

                JSONObject jo2 = new JSONObject();
                jo2.put("address", myCoin.getAddress());
                jo2.put("value", MathUtils.getBigDecimalSubtract(MathUtils.getBigDecimalSubtract(currMoney, pay, 8), "0.00000546", 8));
                outja.put(jo2);
            } else {
                jsonObject.put("outputs_count", 1);
            }

            jsonObject.put("outputs", outja);

            JSONArray usdtja = new JSONArray();
            usdtja.put(31);
            usdtja.put(new BigInteger(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(money, MathUtils.getBigDecimalPow("10", 8, 8), 8), 0)).intValue());
            jsonObject.put("usdt", usdtja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private String getBCHReserved(int i) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();
            for (int j = 0; j < i; j++) {
                jsonArray.put(Long.valueOf(MathUtils.getBigDecimalRundNumber(utxoList.get(j).getAmount(), 0)));
            }

            jsonObject.put("amount", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sx", jsonObject.toString());

        return jsonObject.toString();
    }

    private String getAllKB() {
        return "1";
    }


}
