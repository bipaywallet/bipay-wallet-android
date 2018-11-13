package com.spark.bipaywallet.activity.aboutus;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.agreement.AgreementActivity;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.utils.CommonUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于我们
 */

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.tvAgree)
    TextView tvAgree;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_aboutus;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.about_us));
        tvVersion.setText(getString(R.string.app_name) + " V" + CommonUtils.getVersionName());
    }

    @OnClick({R.id.tvAgree})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        showActivity(AgreementActivity.class, null);
    }


}
