package com.spark.bipaywallet.activity.selectcoin;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择货币
 */

public class SelectCoinActivity extends BaseActivity {
    @BindView(R.id.llCNY)
    LinearLayout llCNY;
    @BindView(R.id.llUSD)
    LinearLayout llUSD;
//    @BindView(R.id.llEUR)
//    LinearLayout llEUR;

    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
//    @BindView(R.id.iv3)
//    ImageView iv3;

    private int moneyCode = 1;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_selectcoin;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
//        selected(iv1);

//        llEUR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selected(iv3);
//            }
//        });
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_select_coin_title));
        moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
        if (moneyCode == 1) selected(iv1);
        else if (moneyCode == 2) selected(iv2);
    }


    @OnClick({R.id.llCNY, R.id.llUSD})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llCNY:
                selected(iv1);
                if (moneyCode != 1) changeMoney(1);
                break;
            case R.id.llUSD:
                selected(iv2);
                if (moneyCode != 2) changeMoney(2);
                break;
        }
    }

    private void changeMoney(int moneyCode) {
        this.moneyCode = moneyCode;
        SharedPreferenceInstance.getInstance().saveMoneyCode(moneyCode);
    }

    private void selected(View view) {
        iv1.setVisibility(View.GONE);
        iv2.setVisibility(View.GONE);
//        iv3.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }
}
