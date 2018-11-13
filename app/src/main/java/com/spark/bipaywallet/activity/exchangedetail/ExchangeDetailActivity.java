package com.spark.bipaywallet.activity.exchangedetail;


import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 兑换详情
 */

public class ExchangeDetailActivity extends BaseActivity implements ExchangeDetailContract.View {
    @BindView(R.id.llExchange)
    LinearLayout llExchange;
    @BindView(R.id.tvTopSellName)
    TextView tvTopSellName;
    @BindView(R.id.tvTopBuyName)
    TextView tvTopBuyName;

    @BindView(R.id.ivStatus)
    ImageView ivStatus;
    @BindView(R.id.ivExchanging)
    ImageView ivExchanging;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.ivConfirming)
    ImageView ivConfirming;

    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvSellNum)
    TextView tvSellNum;
    @BindView(R.id.tvBuyNum)
    TextView tvBuyNum;
    @BindView(R.id.tvOutAddress)
    TextView tvOutAddress;
    @BindView(R.id.tvInAddress)
    TextView tvInAddress;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.tvRate)
    TextView tvRate;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvId)
    TextView tvId;

    @BindView(R.id.dotOne)
    ImageView dotOne;
    @BindView(R.id.lineOne)
    View lineOne;
    @BindView(R.id.dotTwo)
    ImageView dotTwo;
    @BindView(R.id.lineTwo)
    View lineTwo;
    @BindView(R.id.dotThree)
    ImageView dotThree;

    @BindView(R.id.tvSellMoney)
    TextView tvSellMoney;
    @BindView(R.id.tvBuyMoney)
    TextView tvBuyMoney;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private MyHandler myHandler;
    private MyExchangeRecord myExchangeRecord;
    private Animation exchangingAnimation;
    private TranslateAnimation logoAlphaAnimation;

    private ExchangeDetailContract.Presenter presenter;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_exchange_detail;
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
        new ExchangeDetailPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);

        exchangingAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_exchange_detail_exchanging);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        exchangingAnimation.setInterpolator(lin);
        ivExchanging.setAnimation(exchangingAnimation);

        logoAlphaAnimation = new TranslateAnimation(0, 0, 0, 10);
        logoAlphaAnimation.setDuration(1000);
        logoAlphaAnimation.setRepeatCount(Animation.INFINITE);
        logoAlphaAnimation.setRepeatMode(Animation.REVERSE);
        ivLogo.setAnimation(logoAlphaAnimation);

        myHandler = new MyHandler(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString("id");

            if (StringUtils.isNotEmpty(id)) {
                List<MyExchangeRecord> list = DataSupport.where("walletName = ? and exchangeId = ?",
                        MyApplication.getApp().getCurrentWallet().getName(), id).find(MyExchangeRecord.class);

                if (list != null && list.size() > 0) {
                    myExchangeRecord = list.get(0);
                    tvTopSellName.setText(myExchangeRecord.getCurrencyFrom() + " ");
                    tvTopBuyName.setText(" " + myExchangeRecord.getCurrencyTo());
                    tvSellNum.setText(myExchangeRecord.getAmountExpectedFrom() + " ");
                    tvBuyNum.setText(" " + myExchangeRecord.getAmountExpectedTo());
                    tvOutAddress.setText(myExchangeRecord.getPayoutAddress());
                    tvInAddress.setText(myExchangeRecord.getPayinAddress());
                    tvRate.setText(myExchangeRecord.getRate());
                    tvTime.setText(myExchangeRecord.getTime());
                    tvId.setText(myExchangeRecord.getExchangeId());

                    tvSellMoney.setText(myExchangeRecord.getAmountExpectedFrom() + " " + myExchangeRecord.getCurrencyFrom());
                    tvBuyMoney.setText(myExchangeRecord.getAmountExpectedTo() + " " + myExchangeRecord.getCurrencyTo());
                    initStatus(myExchangeRecord.getStatus());
                }
            }
        }
    }

    private void initStatus(String status) {
        switch (status) {
            case "waiting":
                tvStatus.setText(getString(R.string.exchange_confirming));
                hideAll();
                ivLogo.setVisibility(View.VISIBLE);
                ivLogo.setAnimation(logoAlphaAnimation);
                ivConfirming.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_exchange_confirmimg);

                dotOne.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_gray_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotThree.setBackgroundResource(R.drawable.shape_circle_gray_background);
                break;
            case "confirming":
                hideAll();
                ivLogo.setVisibility(View.VISIBLE);
                ivLogo.setAnimation(logoAlphaAnimation);
                ivConfirming.setVisibility(View.VISIBLE);
                tvStatus.setText(getString(R.string.exchange_confirming));
                ivStatus.setImageResource(R.mipmap.icon_exchange_confirmimg);

                dotOne.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_gray_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotThree.setBackgroundResource(R.drawable.shape_circle_gray_background);
                break;
            case "exchanging":
                hideAll();
                ivExchanging.setVisibility(View.VISIBLE);
                ivExchanging.setAnimation(exchangingAnimation);
                tvStatus.setText(getString(R.string.exchange_exchanging));
                ivStatus.setImageResource(R.mipmap.icon_exchange_exchanging);

                dotOne.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.btn_option_normal));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_gray_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotThree.setBackgroundResource(R.drawable.shape_circle_gray_background);
                break;
            case "sending":
                hideAll();
                ivLogo.setVisibility(View.VISIBLE);
                ivLogo.setAnimation(logoAlphaAnimation);
                ivConfirming.setVisibility(View.VISIBLE);
                tvStatus.setText(getString(R.string.exchange_confirming));
                ivStatus.setImageResource(R.mipmap.icon_exchange_confirmimg);

                dotOne.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.btn_option_normal));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotThree.setBackgroundResource(R.drawable.shape_circle_gray_background);
                break;
            case "finished":
                hideAll();
                ivStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(getString(R.string.exchange_finished));
                ivStatus.setImageResource(R.mipmap.icon_exchange_success);

                dotOne.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.btn_option_normal));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_blue_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.btn_option_normal));
                dotThree.setBackgroundResource(R.drawable.shape_circle_blue_background);
                break;
            default:
                hideAll();
                ivStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(getString(R.string.exchange_fail));
                ivStatus.setImageResource(R.mipmap.icon_exchange_fail);

                dotOne.setBackgroundResource(R.drawable.shape_circle_gray_background);
                lineOne.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotTwo.setBackgroundResource(R.drawable.shape_circle_gray_background);
                lineTwo.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.font_sec_grey));
                dotThree.setBackgroundResource(R.drawable.shape_circle_gray_background);
                break;
        }
    }

    private void hideAll() {
        ivExchanging.clearAnimation();
        ivLogo.clearAnimation();
        ivStatus.setVisibility(View.GONE);
        ivExchanging.setVisibility(View.GONE);
        ivLogo.setVisibility(View.GONE);
        ivConfirming.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.tvOutAddress, R.id.tvInAddress, R.id.tvId})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvOutAddress:
                if (!StringUtils.isEmpty(tvOutAddress.getText().toString())) {
                    CommonUtils.copyText(ExchangeDetailActivity.this, tvOutAddress.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
            case R.id.tvInAddress:
                if (!StringUtils.isEmpty(tvInAddress.getText().toString())) {
                    CommonUtils.copyText(ExchangeDetailActivity.this, tvInAddress.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
            case R.id.tvId:
                if (!StringUtils.isEmpty(tvId.getText().toString())) {
                    CommonUtils.copyText(ExchangeDetailActivity.this, tvId.getText().toString());
                    ToastUtils.showToast(R.string.copy_success);
                }
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        if (myExchangeRecord.getStatus().equals("waiting") || myExchangeRecord.getStatus().equals("confirming") ||
                myExchangeRecord.getStatus().equals("exchanging") || myExchangeRecord.getStatus().equals("sending")) {
            presenter.getExchangeStatus(myExchangeRecord.getExchangeId(), getExchangeStatusJson(myExchangeRecord.getExchangeId()));
        } else {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private String getExchangeStatusJson(String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "getStatus");

            JSONObject jo = new JSONObject();
            jo.put("id", id);
            jsonObject.put("params", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void setPresenter(ExchangeDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getExchangeStatusSuccess(String id, Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 1;
        final Bundle bundle = new Bundle();
        bundle.putString("exchangeId", id);
        bundle.putString("status", (String) obj);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void exchangeStatusSuccess(Bundle bundle) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }

        ContentValues values = new ContentValues();
        values.put("status", bundle.getString("status"));
        DataSupport.updateAll(MyExchangeRecord.class, values, "walletName = ? and exchangeId = ?",
                MyApplication.getApp().getCurrentWallet().getName(), bundle.getString("exchangeId"));

        myExchangeRecord.setStatus(bundle.getString("status"));
        initStatus(bundle.getString("status"));
    }

    @Override
    public void getExchangeStatusFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void fail(Bundle bundle) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(ExchangeDetailActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    private static class MyHandler extends Handler {

        private final WeakReference<ExchangeDetailActivity> mActivity;

        private MyHandler(ExchangeDetailActivity helpActivity) {
            mActivity = new WeakReference<>(helpActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExchangeDetailActivity activity = mActivity.get();

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
                        activity.exchangeStatusSuccess(msg.getData());
                        break;
                    case -1:
                        activity.fail(msg.getData());
                        break;
                }
            }
        }
    }


}
