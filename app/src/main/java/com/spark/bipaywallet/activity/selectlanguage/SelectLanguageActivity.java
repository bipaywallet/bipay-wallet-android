package com.spark.bipaywallet.activity.selectlanguage;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.home.MainActivity;
import com.spark.bipaywallet.base.ActivityManage;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择语言
 */

public class SelectLanguageActivity extends BaseActivity {

    @BindView(R.id.llEnglish)
    LinearLayout llEnglish;
    @BindView(R.id.llSimple)
    LinearLayout llSimple;

    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;

    private int languageCode = 1;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_selectlanguage;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_select_language_title));
        languageCode = SharedPreferenceInstance.getInstance().getLanguageCode();
        if (languageCode == 1) selected(iv1);
        else if (languageCode == 2) selected(iv2);
    }

    @OnClick({R.id.llEnglish, R.id.llSimple})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llSimple:
                selected(iv1);
                if (languageCode != 1) language(1);
                break;
            case R.id.llEnglish:
                selected(iv2);
                if (languageCode != 2) language(2);
                break;
        }
    }

    private void language(int languageCode) {
        SharedPreferenceInstance.getInstance().saveLanguageCode(languageCode);
        ActivityManage.finishAll();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void selected(View view) {
        iv1.setVisibility(View.GONE);
        iv2.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }
}
