package com.spark.bipaywallet.activity.exchange;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.exchangeconfirm.ExchangeConfirmActivity;
import com.spark.bipaywallet.activity.exchangerecord.ExchangeRecoedActivity;
import com.spark.bipaywallet.adapter.ExchangeCoinAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.dialog.WhiteCenterDialog;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.NumEditTextUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 兑换
 */

public class ExchangeActivity extends BaseActivity implements ExchangeContract.View {
    @BindView(R.id.ivRecord)
    ImageView ivRecord;
    @BindView(R.id.rlSellCoin)
    RelativeLayout rlSellCoin;
    @BindView(R.id.rlBuyCoin)
    RelativeLayout rlBuyCoin;
    @BindView(R.id.llDefault)
    LinearLayout llDefault;
    @BindView(R.id.llSwitchCoin)
    LinearLayout llSwitchCoin;

    @BindView(R.id.rlTopSell)
    RelativeLayout rlTopSell;
    @BindView(R.id.rlBottomBuy)
    RelativeLayout rlBottomBuy;
    @BindView(R.id.ivSellCoin)
    ImageView ivSellCoin;
    @BindView(R.id.tvSellCoin)
    TextView tvSellCoin;
    @BindView(R.id.ivBuyCoin)
    ImageView ivBuyCoin;
    @BindView(R.id.tvBuyCoin)
    TextView tvBuyCoin;

    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.ivTopSell)
    ImageView ivTopSell;
    @BindView(R.id.tvTopSell)
    TextView tvTopSell;
    @BindView(R.id.ivBottomBuy)
    ImageView ivBottomBuy;
    @BindView(R.id.tvBottomBuy)
    TextView tvBottomBuy;
    @BindView(R.id.ivAsk)
    ImageView ivAsk;

    @BindView(R.id.rvCoin)
    RecyclerView rvCoin;
    @BindView(R.id.tvRate)
    TextView tvRate;
    @BindView(R.id.tvFee)
    TextView tvFee;
    @BindView(R.id.tvRange)
    TextView tvRange;
    @BindView(R.id.ivSwap)
    ImageView ivSwap;

    @BindView(R.id.etSellNum)
    EditText etSellNum;
    @BindView(R.id.etBuyNum)
    EditText etBuyNum;
    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ExchangeContract.Presenter presenter;
    private List<MyCoin> myCoinList;
    private List<MyCoin> tempCoinList = new ArrayList<>();
    private ExchangeCoinAdapter exchangeCoinAdapter;
    private MyHandler myHandler;
    private WhiteCenterDialog whiteCenterDialog;
    private String rate = "0";
    private String minRange = "0";
    private String totalAmount;

    private MyCoin sellCoin;
    private MyCoin buyCoin;

    private boolean isChangeSell = false;
    private boolean isChangeBuy = false;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_exchange;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        ivRecord.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_exchange_title));
        new ExchangePresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        myHandler = new MyHandler(this);
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvCoin.setLayoutManager(manager);
        myCoinList = new ArrayList<>();
        exchangeCoinAdapter = new ExchangeCoinAdapter(this, R.layout.adapter_exchange_coin, myCoinList);
        rvCoin.setAdapter(exchangeCoinAdapter);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_exchange_message, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        exchangeCoinAdapter.setEmptyView(emptyView);

        if (MyApplication.getApp().getCurrentWallet() != null) {
            List<MyCoin> ethList = DataSupport.where("walletName = ? and name = ?",
                    MyApplication.getApp().getCurrentWallet().getName(), "ETH").find(MyCoin.class);

            if (ethList != null && ethList.size() > 0) {
                sellCoin = ethList.get(0);
            }
            if (sellCoin != null && CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()) != null) {
                ivSellCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
                tvSellCoin.setText(sellCoin.getName());

                ivTopSell.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
                tvTopSell.setText(sellCoin.getName());
            }

            List<MyCoin> btcList = DataSupport.where("walletName = ? and name = ?",
                    MyApplication.getApp().getCurrentWallet().getName(), "BTC").find(MyCoin.class);

            if (btcList != null && btcList.size() > 0) {
                buyCoin = btcList.get(0);
            }
            if (buyCoin != null && CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()) != null) {
                ivBuyCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
                tvBuyCoin.setText(buyCoin.getName());

                ivBottomBuy.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
                tvBottomBuy.setText(buyCoin.getName());
            }

            if (sellCoin != null && buyCoin != null) {
                tvRate.setText("0 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                tvRange.setText(sellCoin.getName() + getString(R.string.activity_exchange_min_out) + MathUtils.getBigDecimalRundNumber("0", 8));
                minRange = "0";
            }
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        if (sellCoin != null && buyCoin != null) {
            displayLoadingPopup();
            presenter.getMinAmount(getMinAmountJson(sellCoin.getName(), buyCoin.getName()));
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter.getExchangeAmount(getExchangeAmountJson(sellCoin.getName(), buyCoin.getName(), "1"));
                }
            }, 500);

            if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(params);
            } else if (sellCoin.getCoinType() == 60) {
                HashMap<String, String> params = new HashMap<>();
                params.put("address", sellCoin.getAddress());
                params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getETHMessage(params);
            }
        }
    }

    @OnClick({R.id.rlSellCoin, R.id.rlBuyCoin, R.id.rlClose, R.id.ivAsk, R.id.ivSwap,
            R.id.rlTopSell, R.id.rlBottomBuy, R.id.ivRecord, R.id.tvExchange})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.rlSellCoin:
                refreshCoin();
                myCoinList.clear();
                tempCoinList.clear();
                exchangeCoinAdapter.notifyDataSetChanged();
                llDefault.setVisibility(View.GONE);
                llSwitchCoin.setVisibility(View.VISIBLE);
                rlTopSell.setSelected(true);
                rlBottomBuy.setSelected(false);
                progressBar.setVisibility(View.VISIBLE);
                presenter.getExchangeCoin(getCoinJson());
                break;
            case R.id.rlBuyCoin:
                refreshCoin();
                myCoinList.clear();
                tempCoinList.clear();
                exchangeCoinAdapter.notifyDataSetChanged();
                llDefault.setVisibility(View.GONE);
                llSwitchCoin.setVisibility(View.VISIBLE);
                rlTopSell.setSelected(false);
                rlBottomBuy.setSelected(true);
                progressBar.setVisibility(View.VISIBLE);
                presenter.getExchangeCoin(getCoinJson());
                break;
            case R.id.rlTopSell:
                rlTopSell.setSelected(true);
                rlBottomBuy.setSelected(false);
                int index = -1;
                for (int j = 0; j < myCoinList.size(); j++) {
                    if (myCoinList.get(j).getName().equals(sellCoin.getName())) {
                        index = j;
                        break;
                    }
                }
                exchangeCoinAdapter.setPos(index);
                break;
            case R.id.rlBottomBuy:
                rlTopSell.setSelected(false);
                rlBottomBuy.setSelected(true);
                int index2 = -1;
                for (int j = 0; j < myCoinList.size(); j++) {
                    if (myCoinList.get(j).getName().equals(buyCoin.getName())) {
                        index2 = j;
                        break;
                    }
                }
                exchangeCoinAdapter.setPos(index2);
                break;
            case R.id.rlClose:
                etSearch.setText("");
                llSwitchCoin.setVisibility(View.GONE);
                llDefault.setVisibility(View.VISIBLE);
                break;
            case R.id.ivRecord:
                showActivity(ExchangeRecoedActivity.class, null);
                break;
            case R.id.tvExchange:
                doExchange();
                break;
            case R.id.ivAsk:
                showTipDialog();
                break;
            case R.id.ivSwap:
                doSwap();
                break;
        }
    }

    private void doExchange() {
        if (sellCoin != null && buyCoin != null && sellCoin.getName().equals(buyCoin.getName())) {
            ToastUtils.showToast(getString(R.string.activity_exchange_tip_same_coin));
            return;
        }

        if (StringUtils.isEmpty(etSellNum.getText().toString(), etBuyNum.getText().toString())) {
            ToastUtils.showToast(getString(R.string.activity_exchange_tip_input));
            return;
        }

        if (CommonUtils.checkInternet()) {
            if (sellCoin != null) {
                if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                        sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", sellCoin.getAddress());
                    params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getCoinMessage(params);
                } else if (sellCoin.getCoinType() == 60) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", sellCoin.getAddress());
                    params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getETHMessage(params);
                }
            }
        } else {
            ToastUtils.showToast(getString(R.string.no_network));
            return;
        }

        if (MathUtils.getBigDecimalCompareTo(totalAmount, etSellNum.getText().toString(), 18) < 0) {
            ToastUtils.showToast(R.string.money_not_enough);
            return;
        }

        if (MathUtils.getBigDecimalCompareTo(etSellNum.getText().toString(), minRange, 18) < 0) {
            ToastUtils.showToast(getString(R.string.activity_exchange_tip_min_out));
            return;
        }

        if (MathUtils.getBigDecimalCompareTo(rate, "0", 18) <= 0) {
            return;
        }

        if (sellCoin != null && buyCoin != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("sellCoin", sellCoin);
            bundle.putSerializable("buyCoin", buyCoin);
            bundle.putString("sellNum", etSellNum.getText().toString());
            bundle.putString("buyNum", etBuyNum.getText().toString());
            bundle.putString("rate", rate);
            showActivity(ExchangeConfirmActivity.class, bundle);
        }
    }

    private void doSwap() {
        if (sellCoin != null && buyCoin != null) {
            if (sellCoin.getName().equals(buyCoin.getName())) {
                return;
            }

            setEtSellNum("");
            setEtBuyNum("");
            MyCoin temp = sellCoin;
            sellCoin = buyCoin;
            buyCoin = temp;

            if (sellCoin != null && CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()) != null) {
                ivSellCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
                tvSellCoin.setText(sellCoin.getName());

                ivTopSell.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
                tvTopSell.setText(sellCoin.getName());
            }

            if (buyCoin != null && CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()) != null) {
                ivBuyCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
                tvBuyCoin.setText(buyCoin.getName());

                ivBottomBuy.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
                tvBottomBuy.setText(buyCoin.getName());
            }

            if (sellCoin != null && buyCoin != null) {
                tvRate.setText("0 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                rate = "0";
                tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                tvRange.setText(sellCoin.getName() + getString(R.string.activity_exchange_min_out) + MathUtils.getBigDecimalRundNumber("0", 8));
                minRange = "0";
            }

            if (sellCoin != null && buyCoin != null) {
                displayLoadingPopup();
                presenter.getMinAmount(getMinAmountJson(sellCoin.getName(), buyCoin.getName()));
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.getExchangeAmount(getExchangeAmountJson(sellCoin.getName(), buyCoin.getName(), "1"));
                    }
                }, 500);

                if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                        sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", sellCoin.getAddress());
                    params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getCoinMessage(params);
                } else if (sellCoin.getCoinType() == 60) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", sellCoin.getAddress());
                    params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getETHMessage(params);
                }
            }
        }
    }

    private void showTipDialog() {
        if (whiteCenterDialog == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_exchange_tip, null);
            whiteCenterDialog = new WhiteCenterDialog(this);
            whiteCenterDialog.setContentView(contentView);
            whiteCenterDialog.show();

            TextView tvCancel = contentView.findViewById(R.id.tvCancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whiteCenterDialog.dismiss();
                }
            });
        } else {
            whiteCenterDialog.show();
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        exchangeCoinAdapter.setOnBtnClickListener(new ExchangeCoinAdapter.OnBtnClickListener() {
            @Override
            public void onClick(int pos) {
                llSwitchCoin.setVisibility(View.GONE);
                llDefault.setVisibility(View.VISIBLE);

                setEtSellNum("");
                setEtBuyNum("");

                List<MyCoin> list = DataSupport.where("walletName = ? and name = ?",
                        MyApplication.getApp().getCurrentWallet().getName(), myCoinList.get(pos).getName()).find(MyCoin.class);

                if (list != null && list.size() > 0) {
                    if (rlTopSell.isSelected()) {
                        sellCoin = list.get(0);
                    } else {
                        buyCoin = list.get(0);
                    }
                    refreshCoin();

                    if (sellCoin != null && buyCoin != null) {
                        if (sellCoin.getName().equals(buyCoin.getName())) {
                            tvRate.setText("1 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber("1", 8) + " " + buyCoin.getName());
                            rate = "1";
                            tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                            tvRange.setText(sellCoin.getName() + getString(R.string.activity_exchange_min_out) + MathUtils.getBigDecimalRundNumber("0", 8));
                            minRange = "0";
                        } else {
                            tvRate.setText("0 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                            rate = "0";
                            tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                            tvRange.setText(sellCoin.getName() + getString(R.string.activity_exchange_min_out) + MathUtils.getBigDecimalRundNumber("0", 8));
                            minRange = "0";

                            displayLoadingPopup();
                            presenter.getMinAmount(getMinAmountJson(sellCoin.getName(), buyCoin.getName()));
                            myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    presenter.getExchangeAmount(getExchangeAmountJson(sellCoin.getName(), buyCoin.getName(), "1"));
                                }
                            }, 500);

                            if (sellCoin.getCoinType() == 0 || sellCoin.getCoinType() == 206 || sellCoin.getCoinType() == 207 ||
                                    sellCoin.getCoinType() == 2 || sellCoin.getCoinType() == 208 || sellCoin.getCoinType() == 145) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("address", sellCoin.getAddress());
                                params.put("coinName", sellCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                                presenter.getCoinMessage(params);
                            } else if (sellCoin.getCoinType() == 60) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("address", sellCoin.getAddress());
                                params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                                presenter.getETHMessage(params);
                            }
                        }
                    }
                }

                etSearch.setText("");
            }
        });

        etSellNum.addTextChangedListener(sellNumTextWatcher);
        etBuyNum.addTextChangedListener(buyNumTextWatcher);
        etSearch.addTextChangedListener(searchTextWatcher);
    }

    private MyTextWatcher searchTextWatcher = new MyTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String charSequence = etSearch.getText().toString();

            if (StringUtils.isNotEmpty(charSequence)) {
                myCoinList.clear();
                exchangeCoinAdapter.notifyDataSetChanged();

                for (int i = 0; i < tempCoinList.size(); i++) {
                    if (contain(tempCoinList.get(i).getName(), charSequence)) {
                        myCoinList.add(tempCoinList.get(i));
                    }
                }

                exchangeCoinAdapter.notifyDataSetChanged();

                if (rlTopSell.isSelected()) {
                    int index = -1;
                    for (int j = 0; j < myCoinList.size(); j++) {
                        if (myCoinList.get(j).getName().equals(sellCoin.getName())) {
                            index = j;
                            break;
                        }
                    }
                    exchangeCoinAdapter.setPos(index);
                } else {
                    int index = -1;
                    for (int j = 0; j < myCoinList.size(); j++) {
                        if (myCoinList.get(j).getName().equals(buyCoin.getName())) {
                            index = j;
                            break;
                        }
                    }
                    exchangeCoinAdapter.setPos(index);
                }
            } else {
                myCoinList.clear();
                myCoinList.addAll(tempCoinList);
                exchangeCoinAdapter.notifyDataSetChanged();

                if (rlTopSell.isSelected()) {
                    int index = -1;
                    for (int j = 0; j < myCoinList.size(); j++) {
                        if (myCoinList.get(j).getName().equals(sellCoin.getName())) {
                            index = j;
                            break;
                        }
                    }
                    exchangeCoinAdapter.setPos(index);
                } else {
                    int index = -1;
                    for (int j = 0; j < myCoinList.size(); j++) {
                        if (myCoinList.get(j).getName().equals(buyCoin.getName())) {
                            index = j;
                            break;
                        }
                    }
                    exchangeCoinAdapter.setPos(index);
                }
            }
        }
    };

    private boolean contain(String input, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        boolean result = m.find();
        return result;
    }

    private MyTextWatcher sellNumTextWatcher = new MyTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isDo = NumEditTextUtils.isVaildText(etSellNum, sellNumTextWatcher);

            if (MathUtils.getBigDecimalCompareTo(etSellNum.getText().toString(), "0", 8) > 0) {
                if (isDo) {
                    if (MathUtils.getBigDecimalCompareTo(rate, "0", 8) > 0) {
                        String buyNum = MathUtils.getBigDecimalMultiply(etSellNum.getText().toString(), rate, 8);
                        String fee = MathUtils.getBigDecimalMultiply(buyNum, "0.005", 8);
                        tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber(fee, 8) + " " + buyCoin.getName());
                        setEtBuyNum(buyNum);
                    } else {
                        tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                        setEtBuyNum("");
                        isChangeSell = true;
                        isChangeBuy = false;
                        displayLoadingPopup();
                        presenter.getExchangeAmount(getExchangeAmountJson(sellCoin.getName(), buyCoin.getName(), "1"));
                    }
                }
            } else {
                tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                setEtBuyNum("");
            }
        }
    };

    private void setEtBuyNum(String str) {
        etBuyNum.removeTextChangedListener(buyNumTextWatcher);
        etBuyNum.setText(str);
        etBuyNum.addTextChangedListener(buyNumTextWatcher);
    }

    private MyTextWatcher buyNumTextWatcher = new MyTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isDo = NumEditTextUtils.isVaildText(etBuyNum, buyNumTextWatcher);

            if (MathUtils.getBigDecimalCompareTo(etBuyNum.getText().toString(), "0", 8) > 0) {
                if (isDo) {
                    if (MathUtils.getBigDecimalCompareTo(rate, "0", 8) > 0) {
                        String fee = MathUtils.getBigDecimalMultiply(etBuyNum.getText().toString(), "0.005", 8);
                        tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber(fee, 8) + " " + buyCoin.getName());
                        setEtSellNum(MathUtils.getBigDecimalDivide(etBuyNum.getText().toString(), rate, 8));
                    } else {
                        tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                        setEtSellNum("");
                        isChangeSell = false;
                        isChangeBuy = true;
                        displayLoadingPopup();
                        presenter.getExchangeAmount(getExchangeAmountJson(sellCoin.getName(), buyCoin.getName(), "1"));
                    }
                }
            } else {
                tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber("0", 8) + " " + buyCoin.getName());
                setEtSellNum("");
            }
        }
    };

    private void setEtSellNum(String str) {
        etSellNum.removeTextChangedListener(sellNumTextWatcher);
        etSellNum.setText(str);
        etSellNum.addTextChangedListener(sellNumTextWatcher);
    }

    private void refreshCoin() {
        if (sellCoin != null && CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()) != null) {
            ivSellCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
            tvSellCoin.setText(sellCoin.getName());
            ivTopSell.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(sellCoin.getName()).getResId());
            tvTopSell.setText(sellCoin.getName());
        }

        if (buyCoin != null && CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()) != null) {
            ivBuyCoin.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
            tvBuyCoin.setText(buyCoin.getName());
            ivBottomBuy.setImageResource(CoinTypeEnum.getCoinTypeEnumByName(buyCoin.getName()).getResId());
            tvBottomBuy.setText(buyCoin.getName());
        }
    }

    @Override
    public void setPresenter(ExchangeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getExchangeCoinSuccess(Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 1;
        message.obj = obj;
        myHandler.sendMessage(message);
    }

    private void exchangeCoinSuccess(String[] strings) {
        progressBar.setVisibility(View.GONE);
        hideLoadingPopup();
        myCoinList.clear();
        for (int i = 0; i < strings.length; i++) {
            String coinName = strings[i].toUpperCase();
            CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(coinName);

            if (coinTypeEnum != null && MyApplication.getApp().getCurrentWallet() != null) {
                List<MyCoin> list = DataSupport.where("walletName = ? and name = ?",
                        MyApplication.getApp().getCurrentWallet().getName(), coinName).find(MyCoin.class);

                if (list != null && list.size() > 0) {
                    myCoinList.add(list.get(0));
                }
            }
        }
        tempCoinList.clear();
        tempCoinList.addAll(myCoinList);
        exchangeCoinAdapter.notifyDataSetChanged();

        if (rlTopSell.isSelected()) {
            int index = -1;
            for (int j = 0; j < myCoinList.size(); j++) {
                if (myCoinList.get(j).getName().equals(sellCoin.getName())) {
                    index = j;
                    break;
                }
            }
            exchangeCoinAdapter.setPos(index);
        } else {
            int index = -1;
            for (int j = 0; j < myCoinList.size(); j++) {
                if (myCoinList.get(j).getName().equals(buyCoin.getName())) {
                    index = j;
                    break;
                }
            }
            exchangeCoinAdapter.setPos(index);
        }
    }

    @Override
    public void getExchangeCoinFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void fail(Bundle bundle) {
        progressBar.setVisibility(View.GONE);
        hideLoadingPopup();
        NetCodeUtils.checkedErrorCode(ExchangeActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    @Override
    public void getMinAmountSuccess(Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 2;
        message.obj = obj;
        myHandler.sendMessage(message);
    }

    private void minAmountSuccess(String str) {
        tvRange.setText(sellCoin.getName() + getString(R.string.activity_exchange_min_out) + MathUtils.getBigDecimalRundNumber(str, 8));
        minRange = str;
    }

    @Override
    public void getMinAmountFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    @Override
    public void getExchangeAmountSuccess(Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 3;
        message.obj = obj;
        myHandler.sendMessage(message);
    }

    private void exchangeAmountSuccess(String str) {
        hideLoadingPopup();

        tvRate.setText("1 " + sellCoin.getName() + " ≈ " + MathUtils.getBigDecimalRundNumber(str, 8) + " " + buyCoin.getName());
        rate = str;

        if (isChangeSell) {
            String buyNum = MathUtils.getBigDecimalMultiply(etSellNum.getText().toString(), rate, 8);
            String fee = MathUtils.getBigDecimalMultiply(buyNum, "0.005", 8);
            tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber(fee, 8) + " " + buyCoin.getName());
            setEtBuyNum(buyNum);
        } else if (isChangeBuy) {
            String fee = MathUtils.getBigDecimalMultiply(etBuyNum.getText().toString(), "0.005", 8);
            tvFee.setText("≈ " + MathUtils.getBigDecimalRundNumber(fee, 8) + " " + buyCoin.getName());
            setEtSellNum(MathUtils.getBigDecimalDivide(etBuyNum.getText().toString(), rate, 8));
        }

        isChangeSell = false;
        isChangeBuy = false;
    }

    @Override
    public void getExchangeAmountFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -2;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    @Override
    public void getCoinMessageSuccess(HttpCoinMessage obj, String coinName) {
        if (obj == null) return;
        totalAmount = MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8);
    }

    @Override
    public void getCoinMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getETHMessageSuccess(HttpETHMessage obj, String coinName) {
        if (obj == null) return;
        totalAmount = MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8);
    }

    @Override
    public void getETHMessageFail(Integer code, String toastMessage) {
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    private void exchangeAmountFail(Bundle bundle) {
        progressBar.setVisibility(View.GONE);
        hideLoadingPopup();
        NetCodeUtils.checkedErrorCode(ExchangeActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
        isChangeSell = false;
        isChangeBuy = false;
    }

    private String getCoinJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "getCurrencies");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String getExchangeAmountJson(String from, String to, String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "getExchangeAmount");

            JSONObject jo = new JSONObject();
            jo.put("from", from);
            jo.put("to", to);
            jo.put("amount", amount);
            jsonObject.put("params", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String getMinAmountJson(String from, String to) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "getMinAmount");

            JSONObject jo = new JSONObject();
            jo.put("from", from);
            jo.put("to", to);
            jsonObject.put("params", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ExchangeActivity> mActivity;

        private MyHandler(ExchangeActivity exchangeActivity) {
            mActivity = new WeakReference<>(exchangeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExchangeActivity activity = mActivity.get();

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
                        activity.exchangeCoinSuccess((String[]) msg.obj);
                        break;
                    case 2:
                        activity.minAmountSuccess((String) msg.obj);
                        break;
                    case 3:
                        activity.exchangeAmountSuccess((String) msg.obj);
                        break;
                    case -1:
                        activity.fail(msg.getData());
                        break;
                    case -2:
                        activity.exchangeAmountFail(msg.getData());
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (llSwitchCoin.getVisibility() == View.VISIBLE) {
            etSearch.setText("");
            llSwitchCoin.setVisibility(View.GONE);
            llDefault.setVisibility(View.VISIBLE);
        } else if (whiteCenterDialog != null && whiteCenterDialog.isShowing()) {
            whiteCenterDialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }


}
