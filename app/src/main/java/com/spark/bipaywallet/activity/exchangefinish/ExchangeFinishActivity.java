package com.spark.bipaywallet.activity.exchangefinish;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 兑换完成
 */

public class ExchangeFinishActivity extends BaseActivity {
    @BindView(R.id.tvNext)
    TextView tvNext;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_exchange_finish;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_exchange_title));
    }

    @OnClick(R.id.tvNext)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        finish();
    }
}
