package com.spark.bipaywallet.activity.exchangeconfirm;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.exchangefinish.ExchangeFinishActivity;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.dialog.PasswordDialog;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.HttpExchangeTransaction;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
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
 * 兑换确认
 */

public class ExchangeConfirmActivity extends BaseActivity implements ExchangeConfirmContract.View {
    @BindView(R.id.llExchange)
    LinearLayout llExchange;
    @BindView(R.id.tvTopSellName)
    TextView tvTopSellName;
    @BindView(R.id.tvTopBuyName)
    TextView tvTopBuyName;

    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    @BindView(R.id.tvSellName)
    TextView tvSellName;
    @BindView(R.id.tvSellNum)
    TextView tvSellNum;
    @BindView(R.id.tvSellAddress)
    TextView tvSellAddress;

    @BindView(R.id.ivSellCoin)
    ImageView ivSellCoin;
    @BindView(R.id.tvSellMoney)
    TextView tvSellMoney;
    @BindView(R.id.ivBuyCoin)
    ImageView ivBuyCoin;
    @BindView(R.id.tvBuyMoney)
    TextView tvBuyMoney;

    @BindView(R.id.tvBuyName)
    TextView tvBuyName;
    @BindView(R.id.tvBuyNum)
    TextView tvBuyNum;
    @BindView(R.id.tvBuyAddress)
    TextView tvBuyAddress;

    @BindView(R.id.tvRate)
    TextView tvRate;

    @BindView(R.id.tvCoinName)
    TextView tvCoinName;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tvSeekPro)
    TextView tvSeekPro;//当前手续费

    private MyCoin sellCoin;
    private MyCoin buyCoin;
    private String sellNum;
    private String buyNum;
    private String rate;
    private String currFee = "0";
    private BigInteger defaultPro;
    private MyHandler myHandler;

    private List<HttpCoinMessage.Utxo> utxoList;
    private String totalAmount;
    private int nonce;
    private String tokenTotalAmount;
    private String usdtTotalAmount;
    private int tokenNonce;
    private String sendPayAddress;//调用createTransaction接口获取的支付地址
    private HttpExchangeTransaction httpExchangeTransaction;

    private AES mAes;
    private String passwordStr;
    private SimpleDateFormat timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private ExchangeConfirmContract.Presenter presenter;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_exchange_confirm;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        tvTitle.setVisibility(View.GONE);
        llExchange.setVisibility(View.VISIBLE);

        new ExchangeConfirmPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        myHandler = new MyHandler(this);
        mAes = new AES();
        jniUtil = JNIUtil.getInstance();
        utxoList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sellCoin = (MyCoin) bundle.getSerializable("sellCoin");
            buyCoin = (MyCoin) bundle.getSerializable("buyCoin");
            sellNum = bundle.getString("sellNum");
            buyNum = bundle.getString("buyNum");
            rate = bundle.getString("rate");

            if (sellCoin != null && buyCoin != null) {
                String sellName = sellCoin.getName();
                String buyName = buyCoin.getName();
                tvTopSellName.setText(sellName + " ");
                tvTopBuyName.setText(" " + buyName);

                tvSellName.setText(sellName);
                tvSellNum.setText(sellNum + " " + sellName);
                tvSellAddress.setText(sellCoin.getAddress());

                tvBuyName.setText(buyName);
                tvBuyNum.setText(buyNum + " " + buyName);
                tvBuyAddress.setText(buyCoin.getAddress());

                if (CoinTypeEnum.getCoinTypeEnumByName(sellName) != null) {
                    ivSellCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellName).getResId());
                }

                if (CoinTypeEnum.getCoinTypeEnumByName(buyName) != null) {
                    ivBuyCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyName).getResId());
                }

                tvSellMoney.setText(sellNum);
                tvBuyMoney.setText(buyNum);

                if (StringUtils.isNotEmpty(rate)) {
                    tvRate.setText("1 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber(rate, 8) + " " + buyCoin.getName());
                }
            }
        }
    }

    @Override
    protected void loadData() {
        if (sellCoin != null) {
            if (sellCoin.getCoinType() == 0) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                tvCoinName.setText(" BTC/KB");
                presenter.getCoinMessage(params);
                initSeekBar("0.00001");

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (sellCoin.getCoinType() == 207) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(params);

                HashMap<String, String> usdtParams = new HashMap<>();
                usdtParams.put("address", sellCoin.getAddress());
                usdtParams.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(usdtParams);

                tvCoinName.setText(" BTC/KB");
                initSeekBar("0.00001");

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (sellCoin.getCoinType() == 206 ||
                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(params);

                if (sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 208) {
                    tvCoinName.setText(" " + sellCoin.getName());
                    initSeekBar("0.008");
                } else if (sellCoin.getCoinType() == 2) {
                    tvCoinName.setText(" " + sellCoin.getName() + "/KB");
                    initSeekBar("0.001");
                } else if (sellCoin.getCoinType() == 145) {
                    tvCoinName.setText(" " + sellCoin.getName() + "/KB");
                    initSeekBar("0.00001");
                }

                HashMap<String, String> chargeParams = new HashMap<>();
                chargeParams.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getServiceCharge(chargeParams);
            } else if (sellCoin.getCoinType() == 60) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                tvCoinName.setText(" ETH");
                if (sellCoin.getName().equals("ETH")) {
                    presenter.getETHMessage(params);
                } else {
                    presenter.getETHMessage(params);
                    HashMap<String, String> tokenParams = new HashMap<>();
                    tokenParams.put("address", sellCoin.getAddress());
                    tokenParams.put("contractAddress", sellCoin.getContractAddress());
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

    private void initSeekBar(final String fee) {
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                            sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                        currFee = MathUtils.getBigDecimalAdd(fee,
                                MathUtils.getBigDecimalDivide(String.valueOf(progress), MathUtils.getBigDecimal10Pow8(), 8),
                                8);
                        tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(currFee, 8));
                    } else if (sellCoin.getCoinType() == 60) {
                        currFee = MathUtils.getBigDecimalDivide(MathUtils.getBigDecimalAdd(fee, MathUtils.getBigDecimalDivide(String.valueOf(progress), "100", 18), 18),
                                MathUtils.getBigDecimalPow("10", 9, 18),
                                18);

                        if (sellCoin.getName().equals("ETH")) {
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

            if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
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

                currFee = MathUtils.getBigDecimalMultiply(fee, "2", 8);
                tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(currFee, 8));
            } else if (sellCoin.getCoinType() == 60) {
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
                if (sellCoin.getName().equals("ETH")) {
                    tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "21000", 18), 8));
                } else {
                    tvSeekPro.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, "60000", 18), 8));
                }
            }
        }
    }

    @OnClick({R.id.tvConfirm})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvConfirm:
                if (sellCoin != null && buyCoin != null && StringUtils.isNotEmpty(sellNum)) {
                    if (CommonUtils.checkInternet()) {
                        if (sellCoin != null) {
                            if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 ||
                                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("address", sellCoin.getAddress());
                                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                                presenter.getCoinMessage(params);
                            } else if (sellCoin.getCoinType() == 207) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("address", sellCoin.getAddress());
                                params.put("coinName", "btc");//注意此参数是用来拼接url的，不是传给接口的
                                presenter.getCoinMessage(params);

                                HashMap<String, String> usdtParams = new HashMap<>();
                                usdtParams.put("address", sellCoin.getAddress());
                                usdtParams.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                                presenter.getCoinMessage(usdtParams);
                            } else if (sellCoin.getCoinType() == 60) {
                                HashMap<String, String> ethParams = new HashMap<>();
                                ethParams.put("address", sellCoin.getAddress());
                                ethParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                                if (sellCoin.getName().equals("ETH")) {
                                    presenter.getETHMessage(ethParams);
                                } else {
                                    presenter.getETHMessage(ethParams);
                                    HashMap<String, String> tokenParams = new HashMap<>();
                                    tokenParams.put("address", sellCoin.getAddress());
                                    tokenParams.put("contractAddress", sellCoin.getContractAddress());
                                    tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                                    presenter.getTokenMessage(tokenParams);
                                }
                            }
                        }
                    } else {
                        ToastUtils.showToast(getString(R.string.no_network));
                        return;
                    }

                    doConfirm(sellNum);
                }

                break;
        }
    }

    private void doConfirm(String money) {
        String pay = "0";
        if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 ||
                sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
            pay = MathUtils.getBigDecimalAdd(money, MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), 8);
        } else if (sellCoin.getCoinType() == 207) {
            if (MathUtils.getBigDecimalCompareTo(usdtTotalAmount, money, 18) <= -1
                    || MathUtils.getBigDecimalCompareTo(usdtTotalAmount, "0", 18) == 0) {
                ToastUtils.showToast(R.string.money_not_enough);
                return;
            }
            pay = MathUtils.getBigDecimalAdd(MathUtils.getBigDecimalMultiply(currFee, getAllKB(), 8), "0.00001092", 8);
        } else if (sellCoin.getCoinType() == 60) {
            if (sellCoin.getName().equals("ETH")) {
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
        if (MathUtils.getBigDecimalCompareTo(totalAmount, pay, 18) <= -1
                || MathUtils.getBigDecimalCompareTo(totalAmount, "0", 18) == 0) {
            if (sellCoin.getCoinType() == 207) {
                ToastUtils.showToast(R.string.fee_not_enough);
            } else {
                ToastUtils.showToast(R.string.money_not_enough);
            }
            return;
        }

        showPasswordDialog();
    }

    private void showPasswordDialog() {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                if (mAes != null && MyApplication.getApp().getCurrentWallet() != null && StringUtils.isNotEmpty(password)) {
                    String deString = mAes.decrypt(MyApplication.getApp().getCurrentWallet().getEncrypMasterKey(), password);

                    if (deString != null) {
                        passwordStr = password;
                        displayLoadingPopup();
                        presenter.createTransaction(getExchangeAmountJson());
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

    @Override
    protected void setListener() {
        super.setListener();

    }

    @Override
    public void setPresenter(ExchangeConfirmContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getCoinMessageSuccess(HttpCoinMessage obj, String coinName) {
        if (obj == null) return;

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
                MathUtils.getBigDecimalPow("10", sellCoin.getDecimals(), 8), 8);
        tokenNonce = message.getNonce();
    }

    @Override
    public void getTokenMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    //保存兑换记录
    private void saveRecord() {
        if (httpExchangeTransaction != null && MyApplication.getApp().getCurrentWallet() != null) {
            MyExchangeRecord myExchangeRecord = new MyExchangeRecord();
            myExchangeRecord.setWalletName(MyApplication.getApp().getCurrentWallet().getName());
            myExchangeRecord.setExchangeId(httpExchangeTransaction.getId());
            myExchangeRecord.setTime(timeDF.format(new Date()));
            myExchangeRecord.setStatus("confirming");
            myExchangeRecord.setCurrencyFrom(httpExchangeTransaction.getCurrencyFrom().toUpperCase());
            myExchangeRecord.setCurrencyTo(httpExchangeTransaction.getCurrencyTo().toUpperCase());
            myExchangeRecord.setPayinAddress(buyCoin.getAddress());
            myExchangeRecord.setAmountExpectedFrom(httpExchangeTransaction.getAmountExpectedFrom());
            myExchangeRecord.setPayoutAddress(sellCoin.getAddress());
            myExchangeRecord.setAmountExpectedTo(buyNum);
            myExchangeRecord.setRate(rate);
            //保存
            myExchangeRecord.save();
        }
    }

    //保存转账交易记录
    private void savePayRecord(String txid) {
        MyRecordBean myRecordBean = new MyRecordBean();
        myRecordBean.setCoinName(sellCoin.getName());
        myRecordBean.setAddress(sellCoin.getAddress());
        myRecordBean.setTxid(txid);
        myRecordBean.setBlockHeight(0);
        myRecordBean.setTime(timeDF.format(new Date()));

        Log.i("sx", timeDF.format(new Date()));

        if (sellCoin.getCoinType() == 60) {
            if (sellCoin.getName().equals("ETH")) {
                myRecordBean.setFee(MathUtils.getBigDecimalMultiply(currFee, "21000", 18));
            } else {
                myRecordBean.setFee(MathUtils.getBigDecimalMultiply(currFee, "60000", 18));
                myRecordBean.setContractAddress(sellCoin.getContractAddress());
            }
        } else {
            myRecordBean.setFee(currFee);
        }

        myRecordBean.setValue(MathUtils.getBigDecimalRundNumber(sellNum, 8));
        myRecordBean.setFrom(sellCoin.getAddress());
        myRecordBean.setTo(sendPayAddress);
        myRecordBean.setOut(true);

        //保存交易记录
        myRecordBean.save();
    }

    @Override
    public void transferPaySuccess(Object obj) {
        if (obj != null) {
            Log.i("sx", (String) obj);
            savePayRecord((String) obj);
            saveRecord();
            showActivity(ExchangeFinishActivity.class, null);
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
            savePayRecord((String) obj);
            saveRecord();
            showActivity(ExchangeFinishActivity.class, null);
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
        if (obj == null) return;
        initSeekBar((String) obj);
    }

    @Override
    public void getServiceChargeFail(Integer code, String toastMessage) {

    }

    @Override
    public void createTransactionSuccess(Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 1;
        message.obj = obj;
        myHandler.sendMessage(message);
    }

    private void createSuccess(HttpExchangeTransaction obj) {
        hideLoadingPopup();
        httpExchangeTransaction = obj;
        doPay(obj);
    }

    private void doPay(HttpExchangeTransaction httpExchangeTransaction) {
        sendPayAddress = httpExchangeTransaction.getPayinAddress();
        if (sellCoin == null || StringUtils.isEmpty(sendPayAddress) || StringUtils.isEmpty(sellNum)) {
            return;
        }

        if (MathUtils.getBigDecimalCompareTo(currFee, "0", 18) <= 0) {
            ToastUtils.showToast(R.string.fee_warn);
            return;
        }

        boolean isVerify = jniUtil.verifyCoinAddress(sendPayAddress, sellCoin.getCoinType());
        if (!isVerify) {
            ToastUtils.showToast(R.string.address_not_verify);
            return;
        }

        if (StringUtils.isNotEmpty(passwordStr)) {
            if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 ||
                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208) {
                confirmDVC(sellNum, passwordStr);
            } else if (sellCoin.getCoinType() == 145) {
                confirmBCH(sellNum, passwordStr);
            } else if (sellCoin.getCoinType() == 207) {
                confirmUSDT(sellNum, passwordStr);
            } else if (sellCoin.getCoinType() == 60) {
                if (sellCoin.getName().equals("ETH")) {
                    confirmETH(sellNum, passwordStr);
                } else {
                    confirmToken(sellNum, passwordStr);
                }
            }
        }
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

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), sellCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(sellCoin.getSubPrivKey(), password);
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
            jsonObject.put("from", sellCoin.getAddress());
            jsonObject.put("to", sendPayAddress);
            String v = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(money, MathUtils.getBigDecimalPow("10", sellCoin.getDecimals(), 18), 18), 0);
            jsonObject.put("value", v);
            jsonObject.put("nonce", tokenNonce + "");
            String g = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("gasprice", g);
            jsonObject.put("gas", "60000");
            jsonObject.put("contractAddr", sellCoin.getContractAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sx", jsonObject.toString());

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), sellCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            Log.i("sx", newTransaction);
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(sellCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            Log.i("sx", deSubKey + "sub");
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, sellCoin.getCoinType(), -1);
            if (!StringUtils.isEmpty(signPrivk)) {
                Log.i("sx", signPrivk + "signPrivk");
                jniUtil.freeAlloc(signPrivk);
            }

            if (!StringUtils.isEmpty(newTransaction, signPrivk)) {
                String signature = jniUtil.signatureForTransfer(newTransaction, signPrivk, sellCoin.getCoinType());
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
            jsonObject.put("from", sellCoin.getAddress());
            jsonObject.put("to", sendPayAddress);
            String v = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(money, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("value", v + "");
            jsonObject.put("nonce", nonce + "");
            String g = MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(currFee, MathUtils.getBigDecimalPow("10", 18, 18), 18), 0);
            jsonObject.put("gasprice", g + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sx", jsonObject.toString());

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), sellCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(sellCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, sellCoin.getCoinType(), -1);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
            }

            if (!StringUtils.isEmpty(newTransaction, signPrivk)) {
                String signature = jniUtil.signatureForTransfer(newTransaction, signPrivk, sellCoin.getCoinType());
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
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

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), sellCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(sellCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            StringBuffer stringBuffer = new StringBuffer();
            CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName());
            int keyPrefix = -1;
            if (coinTypeEnum != null) {
                keyPrefix = coinTypeEnum.getKeyPrefix();
            }
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, sellCoin.getCoinType(), keyPrefix);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
                stringBuffer.append(signPrivk);
                for (int k = 0; k < i - 1; k++) {
                    stringBuffer.append(" " + signPrivk);
                }
            }

            Log.i("sx", stringBuffer.toString() + "---stringBuffer");

            if (!StringUtils.isEmpty(newTransaction, stringBuffer.toString())) {
                String signature = jniUtil.signatureForTransfer(newTransaction, stringBuffer.toString(), sellCoin.getCoinType());
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
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

        String newTransaction = jniUtil.createNewTransaction(jsonObject.toString(), sellCoin.getCoinType());
        if (!StringUtils.isEmpty(newTransaction)) {
            jniUtil.freeAlloc(newTransaction);
        }

        String deSubKey = mAes.decrypt(sellCoin.getSubPrivKey(), password);
        if (deSubKey != null) {
            StringBuffer stringBuffer = new StringBuffer();
            CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName());
            int keyPrefix = -1;
            if (coinTypeEnum != null) {
                keyPrefix = coinTypeEnum.getKeyPrefix();
            }
            String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, sellCoin.getCoinType(), keyPrefix);
            if (!StringUtils.isEmpty(signPrivk)) {
                jniUtil.freeAlloc(signPrivk);
                stringBuffer.append(signPrivk);
                for (int k = 0; k < i - 1; k++) {
                    stringBuffer.append(" " + signPrivk);
                }
            }

            Log.i("sx", stringBuffer.toString() + "---stringBuffer");

            if (!StringUtils.isEmpty(newTransaction, stringBuffer.toString())) {
                String signature = jniUtil.SignSignatureForTransfer(newTransaction, stringBuffer.toString(), sellCoin.getCoinType(), getBCHReserved(i));
                if (!StringUtils.isEmpty(signature)) {
                    jniUtil.freeAlloc(signature);
                }
                Log.i("sx", signature + "---signature");

                HashMap<String, String> params = new HashMap<>();
                params.put("signTxStr", signature);
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.transferPay(params);
            }
        }
    }

    @Override
    public void createTransactionFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void createFail(Bundle bundle) {
        hideLoadingPopup();
        NetCodeUtils.checkedErrorCode(ExchangeConfirmActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    private static class MyHandler extends Handler {

        private final WeakReference<ExchangeConfirmActivity> mActivity;

        private MyHandler(ExchangeConfirmActivity helpActivity) {
            mActivity = new WeakReference<>(helpActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExchangeConfirmActivity activity = mActivity.get();

            if (Build.VERSION.SDK_INT >= 17) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
            } else {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
            }

            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.createSuccess((HttpExchangeTransaction) msg.obj);
                        break;
                    case -1:
                        activity.createFail(msg.getData());
                        break;
                }
            }
        }
    }

    private String getExchangeAmountJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "createTransaction");

            JSONObject jo = new JSONObject();
            jo.put("from", sellCoin.getName());
            jo.put("to", buyCoin.getName());
            jo.put("address", buyCoin.getAddress());
            jo.put("amount", sellNum);
            jsonObject.put("params", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
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
            jo.put("address", sendPayAddress);
            jo.put("value", MathUtils.getBigDecimalRundNumber(money, 8));
            outja.put(jo);

            if (MathUtils.getBigDecimalCompareTo(pay, currMoney, 18) != 0) {
                jsonObject.put("outputs_count", 2);

                JSONObject jo2 = new JSONObject();
                jo2.put("address", sellCoin.getAddress());
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
            jo.put("address", sendPayAddress);
            jo.put("value", MathUtils.getBigDecimalRundNumber("0.00000546", 8));
            outja.put(jo);

            if (pay != currMoney) {
                jsonObject.put("outputs_count", 2);

                JSONObject jo2 = new JSONObject();
                jo2.put("address", sellCoin.getAddress());
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
