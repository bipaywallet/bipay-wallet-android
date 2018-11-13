package com.spark.bipaywallet.activity.payment;


import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.receivables.ReceivablesActivity;
import com.spark.bipaywallet.activity.tradedetails.TradeDetailsActivity;
import com.spark.bipaywallet.activity.transferpay.TransferPayActivity;
import com.spark.bipaywallet.adapter.RecordAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyDVCRecord;
import com.spark.bipaywallet.entity.MyETHRecord;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.entity.MyTokenRecord;
import com.spark.bipaywallet.entity.MyUSDTRecord;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.DateUtils;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 转账收款
 */

public class PaymentActivity extends BaseActivity implements PaymentContract.View {
    public static final int COINMSG = 2;
    public static final int ETHMSG = 3;
    public static final int TOKENMSG = 4;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.llPay)
    LinearLayout llPay;
    @BindView(R.id.llReceivables)
    LinearLayout llReceivables;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvCopy)
    TextView tvCopy;
    @BindView(R.id.tvCoin)
    TextView tvCoin;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.tvZjjy)
    TextView tvZjjy;

    private MyCoin myCoin;
    private RecordAdapter adapter;
    private PaymentContract.Presenter presenter;
    private List<MyRecordBean> normalTransactionList;

    private MyHandler myHandler;

    @Override
    protected void onRestart() {
        super.onRestart();
        if (myHandler != null) {
            myHandler.sendEmptyMessage(1);
        }
        refresh();
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        new PaymentPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        myHandler = new MyHandler(this);
        normalTransactionList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvRecord.setLayoutManager(manager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myCoin = (MyCoin) bundle.getSerializable("myCoin");
            if (myCoin != null) {
                setTitle(myCoin.getName());
                tvCoin.setText(MathUtils.getBigDecimalRundNumber(myCoin.getNum(), 8));

                int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
                if (moneyCode == 1) {
                    tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getRate(), 8), 2) + " CNY");
                } else {
                    tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getUsdRate(), 8), 2) + " USD");
                }

                tvAddress.setText(myCoin.getAddress());
                initRv();
                if (myHandler != null) {
                    myHandler.sendEmptyMessage(1);
                }
            }
        }
    }

    private void initRv() {
        adapter = new RecordAdapter(this, R.layout.adapter_record, normalTransactionList);
        rvRecord.setAdapter(adapter);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_wallet, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(emptyView);
    }

    @OnClick({R.id.llPay, R.id.llReceivables, R.id.tvCopy})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        Bundle bundle = new Bundle();
        bundle.putSerializable("myCoin", myCoin);
        switch (v.getId()) {
            case R.id.llPay:
                showActivity(TransferPayActivity.class, bundle);
                break;
            case R.id.llReceivables:
                showActivity(ReceivablesActivity.class, bundle);
                break;
            case R.id.tvCopy:
                CommonUtils.copyText(PaymentActivity.this, tvAddress.getText().toString());
                ToastUtils.showToast(R.string.copy_success);
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                MyRecordBean myRecordBean = normalTransactionList.get(position);
                bundle.putSerializable("myRecordBean", myRecordBean);
                showActivity(TradeDetailsActivity.class, bundle);
            }
        });
    }

    private void refresh() {
        if (myCoin != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("address", myCoin.getAddress());
            params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
            params.put("start", getStartBlockHeight() + "");//查询高度开始位置

            LogUtils.i("getStartBlockHeight===" + getStartBlockHeight());

            if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 ||
                    myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                presenter.transactionRecord(params);

                HashMap<String, String> btcParams = new HashMap<>();
                btcParams.put("address", myCoin.getAddress());
                btcParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(btcParams);
            } else if (myCoin.getCoinType() == 60) {
                HashMap<String, String> ethParams = new HashMap<>();
                ethParams.put("address", myCoin.getAddress());
                ethParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                if (myCoin.getName().equals("ETH")) {
                    presenter.transactionETHRecord(params);
                    presenter.getETHMessage(ethParams);
                } else {
                    HashMap<String, String> tokenParams = new HashMap<>();
                    tokenParams.put("address", myCoin.getAddress());
                    tokenParams.put("contractAddress", myCoin.getContractAddress());
                    tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    tokenParams.put("start", getStartBlockHeight() + "");//查询高度开始位置
                    presenter.transactionTokenRecord(tokenParams);

                    HashMap<String, String> tkParams = new HashMap<>();
                    tkParams.put("address", myCoin.getAddress());
                    tkParams.put("contractAddress", myCoin.getContractAddress());
                    tkParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    presenter.getTokenMessage(tkParams);
                }
            } else if (myCoin.getCoinType() == 207) {
                HashMap<String, String> usdtParams = new HashMap<>();
                usdtParams.put("address", myCoin.getAddress());
                usdtParams.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                presenter.getCoinMessage(usdtParams);

                presenter.transactionUSDTRecord(params);
            }
        }
    }

    private void notifyData() {
        List<MyRecordBean> myRecordBeanList;
        if (myCoin.getName().equals("ETH")) {
            myRecordBeanList = DataSupport.where("address = ? and coinName = ?",
                    myCoin.getAddress(), "ETH").find(MyRecordBean.class);
        } else if (myCoin.getContractAddress() != null) {
            myRecordBeanList = DataSupport.where("address = ? and contractAddress = ? and coinName = ?",
                    myCoin.getAddress(), myCoin.getContractAddress(), myCoin.getName()).find(MyRecordBean.class);
        } else {
            myRecordBeanList = DataSupport.where("address = ? and coinName = ?",
                    myCoin.getAddress(), myCoin.getName()).find(MyRecordBean.class);
        }

        if (myRecordBeanList != null && myRecordBeanList.size() > 0) {
            normalTransactionList.clear();
            normalTransactionList.addAll(myRecordBeanList);
            sort(normalTransactionList);

            for (int i = 0; i < normalTransactionList.size(); i++) {
                if (normalTransactionList.get(i).getBlockHeight() != 0) {
                    ContentValues values = new ContentValues();
                    values.put("blockHeight", normalTransactionList.get(i).getBlockHeight());
                    if (myCoin.getContractAddress() != null) {
                        DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ? and contractAddress = ?",
                                myCoin.getAddress(), myCoin.getName(), myCoin.getContractAddress());
                    } else {
                        DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?",
                                myCoin.getAddress(), myCoin.getName());
                    }

                    myCoin.setBlockHeight(normalTransactionList.get(i).getBlockHeight());
                    break;
                }
            }

            adapter.notifyDataSetChanged();
            tvZjjy.setVisibility(View.VISIBLE);
        } else {
            tvZjjy.setVisibility(View.GONE);
        }
    }

    private long getStartBlockHeight() {
        return myCoin.getBlockHeight();
    }


    @Override
    protected void loadData() {
        if (myCoin != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("address", myCoin.getAddress());
            params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
            params.put("start", getStartBlockHeight() + "");//查询高度开始位置

            Log.i("sx", "getStartBlockHeight===" + getStartBlockHeight());

            if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 ||
                    myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                presenter.transactionRecord(params);
            } else if (myCoin.getCoinType() == 60) {
                if (myCoin.getName().equals("ETH")) {
                    presenter.transactionETHRecord(params);
                } else {
                    HashMap<String, String> tokenParams = new HashMap<>();
                    tokenParams.put("address", myCoin.getAddress());
                    tokenParams.put("contractAddress", myCoin.getContractAddress());
                    tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                    tokenParams.put("start", getStartBlockHeight() + "");//查询高度开始位置
                    presenter.transactionTokenRecord(tokenParams);
                }
            } else if (myCoin.getCoinType() == 207) {
                presenter.transactionUSDTRecord(params);
            }
        }
    }

    @Override
    public void setPresenter(PaymentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getCoinMessageSuccess(HttpCoinMessage obj, String coinName) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;
        if (myHandler != null) {
            Message message = new Message();
            message.what = COINMSG;
            Bundle bundle = new Bundle();
            bundle.putSerializable("obj", obj);
            message.setData(bundle);
            myHandler.sendMessage(message);
        }
    }

    private void setCoinMessage(HttpCoinMessage obj) {
        if (myCoin != null) {
            myCoin.setNum(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8));
            tvCoin.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8), 8));

            int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
            if (moneyCode == 1) {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getRate(), 8), 2) + " CNY");
            } else {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getUsdRate(), 8), 2) + " USD");
            }
        }
    }

    @Override
    public void getCoinMessageFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getETHMessageSuccess(HttpETHMessage obj, String coinName) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;
        if (myHandler != null) {
            Message message = new Message();
            message.what = ETHMSG;
            Bundle bundle = new Bundle();
            bundle.putSerializable("obj", obj);
            message.setData(bundle);
            myHandler.sendMessage(message);
        }
    }

    private void setETHMessage(HttpETHMessage obj) {
        if (myCoin != null) {
            myCoin.setNum(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8));
            tvCoin.setText(MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8), 8));

            int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
            if (moneyCode == 1) {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getRate(), 8), 2) + " CNY");
            } else {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getUsdRate(), 8), 2) + " USD");
            }
        }
    }

    @Override
    public void getETHMessageFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getTokenMessageSuccess(Object obj, String contractAddress) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;
        if (myHandler != null) {
            Message message = new Message();
            message.what = TOKENMSG;
            Bundle bundle = new Bundle();
            bundle.putSerializable("obj", (HttpETHMessage) obj);
            message.setData(bundle);
            myHandler.sendMessage(message);
        }
    }

    private void setTokenMessage(HttpETHMessage message) {
        if (myCoin != null) {
            myCoin.setNum(MathUtils.getBigDecimalDivide(message.getTotalAmount(),
                    MathUtils.getBigDecimalPow("10", myCoin.getDecimals(), 8), 8));
            tvCoin.setText(MathUtils.getBigDecimalRundNumber(myCoin.getNum(), 8));

            int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
            if (moneyCode == 1) {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getRate(), 8), 2) + " CNY");
            } else {
                tvMoney.setText("≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getUsdRate(), 8), 2) + " USD");
            }
        }
    }

    @Override
    public void getTokenMessageFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void transactionRecordSuccess(Object obj) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;

        List<MyDVCRecord> recordList = (List<MyDVCRecord>) obj;
        if (recordList.get(0).getNormalTransactions() != null && recordList.get(0).getNormalTransactions().size() > 0) {
            List<MyDVCRecord.NormalTransaction> currTransactionList = recordList.get(0).getNormalTransactions();
            for (int i = 0; i < currTransactionList.size(); i++) {
                MyDVCRecord.NormalTransaction transaction = currTransactionList.get(i);

                //如果数据库中有地址对应相同的txid记录则删除后添加
                List<MyRecordBean> myRecordBeanList = DataSupport.where("txid = ? and address = ? and coinName = ?",
                        transaction.getTxid(), myCoin.getAddress(), myCoin.getName()).find(MyRecordBean.class);

                String remarkStr = null;
                if (myRecordBeanList != null && myRecordBeanList.size() > 0) {
                    for (int n = 0; n < myRecordBeanList.size(); n++) {
                        remarkStr = myRecordBeanList.get(n).getRemark();
                        DataSupport.deleteAll(MyRecordBean.class, "txid = ? and address = ? and coinName = ?",
                                myRecordBeanList.get(n).getTxid(), myCoin.getAddress(), myCoin.getName());
                    }
                }

                //初始化交易记录
                boolean isOut = false;//是转出
                for (int j = 0; j < transaction.getInputs().size(); j++) {
                    if (MyApplication.getApp().getCurrentWallet() != null) {
                        List<MyCoin> myCoinList = DataSupport.where("address = ? and walletName = ? and name = ?",
                                transaction.getInputs().get(j).getAddress(), MyApplication.getApp().getCurrentWallet().getName(),
                                myCoin.getName()).find(MyCoin.class);

                        if (myCoinList != null && myCoinList.size() > 0) {
                            isOut = true;
                            break;
                        }
                    }
                }
                MyRecordBean myRecordBean = new MyRecordBean();
                myRecordBean.setCoinName(myCoin.getName());
                myRecordBean.setAddress(myCoin.getAddress());
                myRecordBean.setTxid(transaction.getTxid());
                myRecordBean.setBlockHeight(Long.valueOf(transaction.getBlockHeight()));
                myRecordBean.setTime(transaction.getTime());
                myRecordBean.setFee(MathUtils.getBigDecimalDivide(transaction.getFee(), MathUtils.getBigDecimal10Pow8(), 8));
                myRecordBean.setOut(isOut);
                if (StringUtils.isNotEmpty(remarkStr)) {
                    myRecordBean.setRemark(remarkStr);
                }

                if (isOut) {
                    boolean isSet = false;
                    for (int k = 0; k < transaction.getOutputs().size(); k++) {
                        if (MyApplication.getApp().getCurrentWallet() != null) {
                            List<MyCoin> myCoinList = DataSupport.where("address = ? and walletName = ? and name = ?",
                                    transaction.getOutputs().get(k).getAddress(), MyApplication.getApp().getCurrentWallet().getName(),
                                    myCoin.getName()).find(MyCoin.class);

                            if (myCoinList == null || myCoinList.size() == 0) {
                                myRecordBean.setValue(MathUtils.getBigDecimalDivide(transaction.getOutputs().get(k).getAmount(), MathUtils.getBigDecimal10Pow8(), 8));
                                myRecordBean.setTo(transaction.getOutputs().get(k).getAddress());
                                isSet = true;
                                break;
                            }
                        }
                    }

                    if (!isSet) {
                        myRecordBean.setValue("0.0");
                        myRecordBean.setTo(myCoin.getAddress());
                    }
                } else {
                    for (int m = 0; m < transaction.getOutputs().size(); m++) {
                        List<MyCoin> myCoinList = DataSupport.where("address = ? and name = ?",
                                transaction.getOutputs().get(m).getAddress(), myCoin.getName()).find(MyCoin.class);

                        if (myCoinList != null && myCoinList.size() > 0) {
                            myRecordBean.setValue(MathUtils.getBigDecimalDivide(transaction.getOutputs().get(m).getAmount(), MathUtils.getBigDecimal10Pow8(), 8));
                            myRecordBean.setTo(transaction.getOutputs().get(m).getAddress());
                            break;
                        }
                    }
                }

                //保存交易记录
                myRecordBean.save();
            }

            if (myHandler != null) {
                myHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void transactionRecordFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void transactionUSDTRecordSuccess(Object obj) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;

        List<MyUSDTRecord> recordList = (List<MyUSDTRecord>) obj;
        if (recordList.size() > 0) {
            for (int i = 0; i < recordList.size(); i++) {
                MyUSDTRecord myUSDTRecord = recordList.get(i);

                //如果数据库中有地址对应相同的txid记录则删除后添加
                List<MyRecordBean> myRecordBeanList = null;
                myRecordBeanList = DataSupport.where("txid = ? and address = ? and coinName = ?",
                        myUSDTRecord.getTxid(), myCoin.getAddress(), myCoin.getName()).find(MyRecordBean.class);

                String remarkStr = null;
                if (myRecordBeanList != null && myRecordBeanList.size() > 0) {
                    for (int n = 0; n < myRecordBeanList.size(); n++) {
                        remarkStr = myRecordBeanList.get(n).getRemark();
                        DataSupport.deleteAll(MyRecordBean.class, "txid = ? and address = ? and coinName = ?",
                                myRecordBeanList.get(n).getTxid(), myCoin.getAddress(), myCoin.getName());
                    }
                }

                //初始化交易记录
                boolean isOut = false;//是转出
                if (MyApplication.getApp().getCurrentWallet() != null) {
                    List<MyCoin> myCoinList = DataSupport.where("address = ? and walletName = ? and name = ?", myUSDTRecord.getSendingAddress(),
                            MyApplication.getApp().getCurrentWallet().getName(), myCoin.getName()).find(MyCoin.class);
                    if (myCoinList != null && myCoinList.size() > 0) {
                        isOut = true;
                    }
                }
                MyRecordBean myRecordBean = new MyRecordBean();
                myRecordBean.setCoinName(myCoin.getName());
                myRecordBean.setAddress(myCoin.getAddress());
                myRecordBean.setTxid(myUSDTRecord.getTxid());
                myRecordBean.setBlockHeight(Long.valueOf(myUSDTRecord.getBlockHeight()));
                myRecordBean.setTime(DateUtils.getFormatTime(null, new Date(Long.valueOf(myUSDTRecord.getBlockTime()) * 1000)));
                myRecordBean.setFee(MathUtils.getBigDecimalDivide(myUSDTRecord.getFee(), MathUtils.getBigDecimal10Pow8(), 8));
                myRecordBean.setValue(MathUtils.getBigDecimalDivide(myUSDTRecord.getAmount(), MathUtils.getBigDecimal10Pow8(), 8));
                myRecordBean.setFrom(myUSDTRecord.getSendingAddress());
                myRecordBean.setTo(myUSDTRecord.getReferenceAddress());
                myRecordBean.setOut(isOut);
                if (StringUtils.isNotEmpty(remarkStr)) {
                    myRecordBean.setRemark(remarkStr);
                }

                //保存交易记录
                myRecordBean.save();
            }

            if (myHandler != null) {
                myHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void transactionUSDTRecordFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void transactionETHRecordSuccess(Object obj) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;

        List<MyETHRecord> recordList = (List<MyETHRecord>) obj;
        if (recordList.get(0).getNormalTransactions() != null && recordList.get(0).getNormalTransactions().size() > 0) {
            List<MyETHRecord.ETHNormalTransaction> currTransactionList = recordList.get(0).getNormalTransactions();
            for (int i = 0; i < currTransactionList.size(); i++) {
                MyETHRecord.ETHNormalTransaction transaction = currTransactionList.get(i);

                //如果数据库中有地址对应相同的txid记录则删除后添加
                List<MyRecordBean> myRecordBeanList;
//                if (myCoin.getContractAddress() != null) {
//                    myRecordBeanList = DataSupport.where("txid = ? and address = ? and coinName = ? and contractAddress = ?",
//                            transaction.getTxid(), myCoin.getAddress(), myCoin.getName(), myCoin.getContractAddress()).find(MyRecordBean.class);
//                } else {
                myRecordBeanList = DataSupport.where("txid = ? and address = ? and coinName = ?",
                        transaction.getTxid(), myCoin.getAddress(), myCoin.getName()).find(MyRecordBean.class);
//                }

                String remarkStr = null;
                if (myRecordBeanList != null && myRecordBeanList.size() > 0) {
                    for (int n = 0; n < myRecordBeanList.size(); n++) {
                        remarkStr = myRecordBeanList.get(n).getRemark();
//                        if (myCoin.getContractAddress() != null) {
//                            DataSupport.deleteAll(MyRecordBean.class, "txid = ? and address = ? and coinName = ? and contractAddress = ?",
//                                    myRecordBeanList.get(n).getTxid(), myCoin.getAddress(), myCoin.getName(), myCoin.getContractAddress());
//                        } else {
                        DataSupport.deleteAll(MyRecordBean.class, "txid = ? and address = ? and coinName = ?",
                                myRecordBeanList.get(n).getTxid(), myCoin.getAddress(), myCoin.getName());
//                        }
                    }
                }

                //初始化交易记录
                boolean isOut = false;//是转出
                if (MyApplication.getApp().getCurrentWallet() != null) {
                    List<MyCoin> myCoinList = DataSupport.where("address = ? and walletName = ? and name = ?", transaction.getFrom(),
                            MyApplication.getApp().getCurrentWallet().getName(), myCoin.getName()).find(MyCoin.class);
                    if (myCoinList != null && myCoinList.size() > 0) {
                        isOut = true;
                    }
                }
                MyRecordBean myRecordBean = new MyRecordBean();
                myRecordBean.setCoinName(myCoin.getName());
                myRecordBean.setAddress(myCoin.getAddress());
                myRecordBean.setTxid(transaction.getTxid());
                myRecordBean.setBlockHeight(Long.valueOf(transaction.getBlockHeight()));
                myRecordBean.setTime(transaction.getTime());
                myRecordBean.setFee(MathUtils.getBigDecimalDivide(transaction.getFee(), MathUtils.getBigDecimal10Pow18(), 8));
                myRecordBean.setValue(MathUtils.getBigDecimalDivide(transaction.getAmount(), MathUtils.getBigDecimal10Pow18(), 8));
                myRecordBean.setFrom(transaction.getFrom());
                myRecordBean.setTo(transaction.getTo());
                myRecordBean.setOut(isOut);
                if (StringUtils.isNotEmpty(remarkStr)) {
                    myRecordBean.setRemark(remarkStr);
                }
//                if (myCoin.getContractAddress() != null) {
//                    myRecordBean.setContractAddress(myCoin.getContractAddress());
//                }

                //保存交易记录
                myRecordBean.save();
            }

            if (myHandler != null) {
                myHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void transactionETHRecordFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void transactionTokenRecordSuccess(Object obj) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (obj == null) return;

        List<MyTokenRecord> recordList = (List<MyTokenRecord>) obj;
        if (recordList.get(0).getNormalTransactions() != null && recordList.get(0).getNormalTransactions().size() > 0) {
            List<MyTokenRecord.TokenNormalTransaction> currTransactionList = recordList.get(0).getNormalTransactions();
            for (int i = 0; i < currTransactionList.size(); i++) {
                MyTokenRecord.TokenNormalTransaction transaction = currTransactionList.get(i);

                //如果数据库中有地址对应相同的txid记录则删除后添加
                List<MyRecordBean> myRecordBeanList = null;
                if (myCoin.getContractAddress() != null) {
                    myRecordBeanList = DataSupport.where("txid = ? and address = ? and contractAddress = ? and coinName = ?",
                            transaction.getTxid(), myCoin.getAddress(), myCoin.getContractAddress(), myCoin.getName()).find(MyRecordBean.class);
                }

                String remarkStr = null;
                if (myRecordBeanList != null && myRecordBeanList.size() > 0) {
                    for (int n = 0; n < myRecordBeanList.size(); n++) {
                        if (myCoin.getContractAddress() != null) {
                            remarkStr = myRecordBeanList.get(n).getRemark();
                            DataSupport.deleteAll(MyRecordBean.class, "txid = ? and address = ? and contractAddress = ? and coinName = ?",
                                    myRecordBeanList.get(n).getTxid(), myCoin.getAddress(), myCoin.getContractAddress(), myCoin.getName());
                        }
                    }
                }

                //初始化交易记录
                boolean isOut = false;//是转出
                if (MyApplication.getApp().getCurrentWallet() != null) {
                    List<MyCoin> myCoinList = DataSupport.where("address = ? and walletName = ? and name = ?", transaction.getFrom(),
                            MyApplication.getApp().getCurrentWallet().getName(), myCoin.getName()).find(MyCoin.class);
                    if (myCoinList != null && myCoinList.size() > 0) {
                        isOut = true;
                    }
                }
                MyRecordBean myRecordBean = new MyRecordBean();
                myRecordBean.setCoinName(myCoin.getName());
                myRecordBean.setAddress(myCoin.getAddress());
                myRecordBean.setTxid(transaction.getTxid());
                myRecordBean.setBlockHeight(Long.valueOf(transaction.getBlockHeight()));
                myRecordBean.setTime(transaction.getTime());
                myRecordBean.setFee(MathUtils.getBigDecimalDivide(transaction.getFee(), MathUtils.getBigDecimal10Pow18(), 8));
                myRecordBean.setValue(MathUtils.getBigDecimalDivide(transaction.getValue(), MathUtils.getBigDecimalPow("10", myCoin.getDecimals(), 8), 8));
                myRecordBean.setFrom(transaction.getFrom());
                myRecordBean.setTo(transaction.getTo());
                myRecordBean.setOut(isOut);
                myRecordBean.setContractAddress(transaction.getContractAddress());
                if (StringUtils.isNotEmpty(remarkStr)) {
                    myRecordBean.setRemark(remarkStr);
                }

                //保存交易记录
                myRecordBean.save();
            }

            if (myHandler != null) {
                myHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void transactionTokenRecordFail(Integer code, String toastMessage) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    //时间排序
    public void sort(List<MyRecordBean> myRecordBeanList) {
        // 排序
        Collections.sort(myRecordBeanList, new Comparator<MyRecordBean>() {
            @Override
            public int compare(MyRecordBean lhs, MyRecordBean rhs) {
                return -lhs.getTime().compareTo(rhs.getTime());
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<PaymentActivity> mActivity;

        private MyHandler(PaymentActivity paymentActivity) {
            mActivity = new WeakReference<>(paymentActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PaymentActivity activity = mActivity.get();

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
                        activity.notifyData();
                        break;
                    case COINMSG:
                        activity.setCoinMessage((HttpCoinMessage) msg.getData().get("obj"));
                        break;
                    case ETHMSG:
                        activity.setETHMessage((HttpETHMessage) msg.getData().get("obj"));
                        break;
                    case TOKENMSG:
                        activity.setTokenMessage((HttpETHMessage) msg.getData().get("obj"));
                        break;
                }
            }
        }
    }


}
