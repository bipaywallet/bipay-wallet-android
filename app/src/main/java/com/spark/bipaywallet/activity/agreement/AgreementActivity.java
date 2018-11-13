package com.spark.bipaywallet.activity.agreement;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;

import butterknife.BindView;

/**
 * 服务条款
 */

public class AgreementActivity extends BaseActivity {
    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_agreement;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_agreement));
    }

}
