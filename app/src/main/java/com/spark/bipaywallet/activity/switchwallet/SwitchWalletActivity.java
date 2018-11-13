package com.spark.bipaywallet.activity.switchwallet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.SwitchWalletAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 交易记录里切换钱包
 */

public class SwitchWalletActivity extends BaseActivity {
    @BindView(R.id.rvWallet)
    RecyclerView rvWallet;
    private SwitchWalletAdapter adapter;
    private List<Wallet> walletList;

    private int currPosition = -1;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SwitchWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_switch_wallet;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_switch_wallet_title));
        initRv();
        String name = SharedPreferenceInstance.getInstance().getWalletRecordName();
        if (name != null) {
            for (int i = 0; i < walletList.size(); i++) {
                if (name.equalsIgnoreCase(walletList.get(i).getName())) {
                    currPosition = i;
                    break;
                }
            }
        }
        adapter.select(currPosition);
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvWallet.setLayoutManager(manager);
        walletList = DataSupport.findAll(Wallet.class);
        if (walletList == null) {
            walletList = new ArrayList<>();
        }
        adapter = new SwitchWalletAdapter(this, walletList);
        rvWallet.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currPosition != position) {
                    SharedPreferenceInstance.getInstance().saveWalletRecordName(walletList.get(position).getName());
                    MyApplication.getApp().setRecordChangeWalletName(true);
                    finish();
                }
            }
        });
    }
}
