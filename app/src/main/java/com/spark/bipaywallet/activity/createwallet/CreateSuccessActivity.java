package com.spark.bipaywallet.activity.createwallet;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包创建成功
 */

public class CreateSuccessActivity extends BaseActivity {
    @BindView(R.id.tvNext)
    TextView tvNext;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_create_wallet_success;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_one_pop_cjqb));
    }

    @OnClick(R.id.tvNext)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        finish();
    }
}
