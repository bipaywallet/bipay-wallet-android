package com.spark.bipaywallet.activity.transferpay;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交易成功
 */

public class TransferPaySuccessActivity extends BaseActivity {
    @BindView(R.id.tvNext)
    TextView tvNext;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_transferpay_success;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_payment_zhuanzhang));
    }

    @OnClick(R.id.tvNext)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        finish();
    }
}
