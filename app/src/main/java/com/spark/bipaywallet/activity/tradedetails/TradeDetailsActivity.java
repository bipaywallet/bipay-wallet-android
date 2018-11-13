package com.spark.bipaywallet.activity.tradedetails;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.searchtrade.SearchTradeActivity;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交易详情
 */

public class TradeDetailsActivity extends BaseActivity {
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.tvReceivables)
    TextView tvReceivables;
    @BindView(R.id.tvTurnOut)
    TextView tvTurnOut;
    @BindView(R.id.tvFee)
    TextView tvFee;
    @BindView(R.id.tvBlockHeight)
    TextView tvBlockHeight;
    @BindView(R.id.tvTradeNumber)
    TextView tvTradeNumber;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvRemark)
    TextView tvRemark;
    @BindView(R.id.tvTrade)
    TextView tvTrade;

    @BindView(R.id.llTurnOut)
    LinearLayout llTurnOut;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.llReMark)
    LinearLayout llReMark;
    @BindView(R.id.lineRemark)
    View lineRemark;
    private MyRecordBean myRecordBean;
//    private String searchWalletName;
//    private int coinType;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_tradedetails;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @OnClick({R.id.tvTradeNumber, R.id.tvTurnOut, R.id.tvReceivables, R.id.tvSearch})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvReceivables:
                if (!StringUtils.isEmpty(tvReceivables.getText().toString())) {
                    CommonUtils.copyText(TradeDetailsActivity.this, tvReceivables.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
            case R.id.tvTurnOut:
                if (!StringUtils.isEmpty(tvTurnOut.getText().toString())) {
                    CommonUtils.copyText(TradeDetailsActivity.this, tvTurnOut.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
            case R.id.tvTradeNumber:
                if (!StringUtils.isEmpty(tvTradeNumber.getText().toString())) {
                    CommonUtils.copyText(TradeDetailsActivity.this, tvTradeNumber.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
            case R.id.tvSearch:
                String tradeNumberStr = tvTradeNumber.getText().toString();
                if (StringUtils.isNotEmpty(tradeNumberStr)) {
                    Bundle bundle = new Bundle();
                    if (myRecordBean.getCoinName().equalsIgnoreCase("BTC")) {
                        bundle.putString("url", "https://btc.com/" + tradeNumberStr);
                    } else if (myRecordBean.getCoinName().equalsIgnoreCase("USDT")) {
                        bundle.putString("url", "https://omniexplorer.info/tx/" + tradeNumberStr);
                    } else if (myRecordBean.getCoinName().equalsIgnoreCase("DVC") ||
                            myRecordBean.getCoinName().equalsIgnoreCase("XNE")) {
                        ToastUtils.showToast(R.string.can_not_search);
                        return;
                    } else if (myRecordBean.getCoinName().equalsIgnoreCase("BCH")) {
                        bundle.putString("url", "https://bch.btc.com/" + tradeNumberStr);
                    } else if (myRecordBean.getCoinName().equalsIgnoreCase("LTC")) {
                        bundle.putString("url", "https://live.blockcypher.com/ltc/tx/" + tradeNumberStr);
                    } else {
                        bundle.putString("url", "https://etherscan.io/tx/" + tradeNumberStr);
                    }
                    showActivity(SearchTradeActivity.class, bundle);
                }
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_trade_details_title));
        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myRecordBean = (MyRecordBean) bundle.getSerializable("myRecordBean");
            if (myRecordBean != null) {
                if (CoinTypeEnum.getCoinTypeEnumByName(myRecordBean.getCoinName()) != null) {
                    int coinType = CoinTypeEnum.getCoinTypeEnumByName(myRecordBean.getCoinName()).getType();
                    if (coinType == 0 || coinType == 206 ||
                            coinType == 2 || coinType == 208 || coinType == 145) {
                        llTurnOut.setVisibility(View.GONE);
                        line1.setVisibility(View.GONE);
                    }
                }

                if (!StringUtils.isEmpty(myRecordBean.getRemark())) {
                    tvRemark.setText(myRecordBean.getRemark());
                } else {
                    llReMark.setVisibility(View.GONE);
                    lineRemark.setVisibility(View.GONE);
                }

                if (myRecordBean.isOut()) {
                    ivIcon.setImageResource(R.mipmap.icon_pay);
                    tvMoney.setText("-" + MathUtils.getBigDecimalRundNumber(myRecordBean.getValue(), 8) + " " + myRecordBean.getCoinName());
                    tvTrade.setText(getResources().getString(R.string.activity_payment_zhuanzhang));
                } else {
                    ivIcon.setImageResource(R.mipmap.icon_receivables);
                    tvMoney.setText("+" + MathUtils.getBigDecimalRundNumber(myRecordBean.getValue(), 8) + " " + myRecordBean.getCoinName());
                    tvTrade.setText(getResources().getString(R.string.activity_payment_shoukuan));
                }

                if (!StringUtils.isEmpty(myRecordBean.getTo())) {
                    tvReceivables.setText(myRecordBean.getTo());
                }

                if (!StringUtils.isEmpty(myRecordBean.getFrom())) {
                    tvTurnOut.setText(myRecordBean.getFrom());
                }

                tvFee.setText(MathUtils.getBigDecimalRundNumber(myRecordBean.getFee(), 8));

                if (myRecordBean.getBlockHeight() == 0) {
                    tvBlockHeight.setText("--");
                } else {
                    tvBlockHeight.setText(myRecordBean.getBlockHeight() + "");
                }

                if (!StringUtils.isEmpty(myRecordBean.getTxid())) {
                    tvTradeNumber.setText(myRecordBean.getTxid());
                } else {
                    tvTradeNumber.setText("--");
                }

                tvTime.setText(myRecordBean.getTime());

                tvStatus.setText(myRecordBean.getBlockHeight() == 0 ? getString(R.string.record_wait_confirm) :
                        getString(R.string.record_finish));

                tvStatus.setTextColor(myRecordBean.getBlockHeight() == 0 ? getResources().getColor(R.color.txtRed) :
                        getResources().getColor(R.color.font_sec_grey));
            }
        }

//        baseNormalTransaction = (BaseNormalTransaction) getIntent().getSerializableExtra("normalTransaction");
//        searchWalletName = getIntent().getStringExtra("searchWalletName");
//        coinType = getIntent().getIntExtra("coinType", -1);

//        if (coinType == 0 || coinType == 206) {
//            MyDVCRecord.NormalTransaction normalTransaction = (MyDVCRecord.NormalTransaction) baseNormalTransaction;
//
//            if (normalTransaction != null && searchWalletName != null) {
//                boolean isOut = false;//是转出
//                for (int i = 0; i < normalTransaction.getInputs().size(); i++) {
//                    List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                            searchWalletName, normalTransaction.getInputs().get(i).getAddress()).find(MyCoin.class);
//
//                    if (myCoinList != null && myCoinList.size() > 0) {
//                        isOut = true;
//                        break;
//                    }
//                }
//
//                if (isOut) {//转出
//                    boolean isSet = false;
//                    for (int i = 0; i < normalTransaction.getOutputs().size(); i++) {
//                        List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                                searchWalletName, normalTransaction.getOutputs().get(i).getAddress()).find(MyCoin.class);
//
//                        if (myCoinList == null || myCoinList.size() == 0) {
//                            tvMoney.setText("-" + WonderfulMathUtils.getRundFormat(normalTransaction.getOutputs().get(i).getAmount()));
////                        tvReceivables.setText(normalTransaction.getOutputs().get(i).getAddress());
//                            isSet = true;
//                            tvFee.setText(WonderfulMathUtils.getRundFormat(normalTransaction.getFee()));
//                            break;
//                        }
//                    }
//
//                    if (!isSet) {
//                        tvMoney.setText("-0.0");
//                        tvFee.setText("0.0");
//                    }
//                } else {//转入
//                    for (int i = 0; i < normalTransaction.getOutputs().size(); i++) {
//                        List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                                searchWalletName, normalTransaction.getOutputs().get(i).getAddress()).find(MyCoin.class);
//
//                        if (myCoinList != null && myCoinList.size() > 0) {
//                            tvMoney.setText("+" + WonderfulMathUtils.getRundFormat(normalTransaction.getOutputs().get(i).getAmount()));
////                        tvReceivables.setText(normalTransaction.getOutputs().get(i).getAddress());
//                            break;
//                        }
//                    }
//
//                    tvFee.setText(WonderfulMathUtils.getRundFormat(normalTransaction.getFee()));
//                }
//
////            tvTurnOut.setText(normalTransaction.getInputs().get(0).getAddress());
//                tvBlockHeight.setText(normalTransaction.getBlockHeight());
//                tvTradeNumber.setText(normalTransaction.getTxid());
//                tvTime.setText(normalTransaction.getTime());
//            }
//        } else if (coinType == 60) {
//            MyETHRecord.ETHNormalTransaction normalTransaction = (MyETHRecord.ETHNormalTransaction) baseNormalTransaction;
//
//            boolean isOut = false;//是转出
//
//            List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                    searchWalletName, normalTransaction.getFrom()).find(MyCoin.class);
//
//            if (myCoinList != null && myCoinList.size() > 0) {
//                isOut = true;
//            }
//
//            if (isOut) {
//                tvMoney.setText("-" + WonderfulMathUtils.getRundFormat(normalTransaction.getValue()));
//            } else {
//                tvMoney.setText("+" + WonderfulMathUtils.getRundFormat(normalTransaction.getValue()));
//            }
//
//            tvFee.setText(WonderfulMathUtils.getRundFormat(normalTransaction.getFee()));
//            tvBlockHeight.setText(normalTransaction.getBlockHeight());
//            tvTradeNumber.setText(normalTransaction.getTxid());
//            tvTime.setText(normalTransaction.getTime());
//        }

    }

    @Override
    protected void loadData() {

    }
}
