package com.spark.bipaywallet.activity.setting;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.selectcoin.SelectCoinActivity;
import com.spark.bipaywallet.activity.selectlanguage.SelectLanguageActivity;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 系统设置
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.llLanguage)
    LinearLayout llLanguage;
    @BindView(R.id.llCoin)
    LinearLayout llCoin;
    @BindView(R.id.llCache)
    LinearLayout llCache;
    @BindView(R.id.tvLanguage)
    TextView tvLanguage;
    @BindView(R.id.tvMoney)
    TextView tvMoney;

    @Override
    protected void onRestart() {
        super.onRestart();
        int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
        if (moneyCode == 1) tvMoney.setText("CNY");
        else if (moneyCode == 2) tvMoney.setText("USD");
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_four_coin));
        int languageCode = SharedPreferenceInstance.getInstance().getLanguageCode();
        if (languageCode == 1) tvLanguage.setText("简体中文");
        else if (languageCode == 2) tvLanguage.setText("English");

        int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
        if (moneyCode == 1) tvMoney.setText("CNY");
        else if (moneyCode == 2) tvMoney.setText("USD");
    }

    @OnClick({R.id.llLanguage, R.id.llCoin, R.id.llCache})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llLanguage:
                showActivity(SelectLanguageActivity.class, null);
                break;
            case R.id.llCoin:
                showActivity(SelectCoinActivity.class, null);
                break;
            case R.id.llCache:
                showClearCacheDialog();
                break;
        }
    }

    private void showClearCacheDialog() {
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.warm_prompt)).btnText(getString(R.string.dialog_cancel), getString(R.string.dialog_confirm)).titleTextColor(getResources().getColor(R.color.black))
                .btnTextColor(getResources().getColor(R.color.dialog_cancel_txt), getResources().getColor(R.color.dialog_ok_txt))
                .content(getString(R.string.dialog_clear_cache_title)).contentTextColor(getResources().getColor(R.color.font_sec_black)).setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();

                    }
                });
        dialog.show();
    }

}
