package com.spark.bipaywallet.activity.addCoin;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.CoinAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.StringUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * 添加资产
 */

public class AddCoinActivity extends BaseActivity {
    @BindView(R.id.rvCoin)
    RecyclerView rvCoin;
    @BindView(R.id.rvERC)
    RecyclerView rvERC;
    @BindView(R.id.tvERC)
    TextView tvERC;
    private CoinAdapter adapter;
    private CoinAdapter recAdapter;
    private List<MyCoin> myCoinList;
    private List<MyCoin> recList;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_addcoin;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_add_coin_title));
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {// 避免嵌套ScrollView滑动卡顿的问题
                return false;
            }
        };
        rvCoin.setLayoutManager(manager);
        LinearLayoutManager manager2 = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {// 避免嵌套ScrollView滑动卡顿的问题
                return false;
            }
        };
        rvERC.setLayoutManager(manager2);

        myCoinList = new ArrayList<>();
        recList = new ArrayList<>();

        adapter = new CoinAdapter(this, R.layout.item_coin, myCoinList);
        rvCoin.setAdapter(adapter);

        recAdapter = new CoinAdapter(this, R.layout.item_coin, recList);
        rvERC.setAdapter(recAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setOnBtnClickListener(new CoinAdapter.OnBtnClickListener() {
            @Override
            public void onClick(int pos, boolean isSelected) {
                MyApplication.getApp().setChangeWallet(true);
                MyCoin myCoin = myCoinList.get(pos);
                ContentValues values = new ContentValues();
                values.put("isAdded", isSelected);
                DataSupport.updateAll(MyCoin.class, values, "walletName = ? and name = ?", myCoin.getWalletName(), myCoin.getName());
            }
        });

        recAdapter.setOnBtnClickListener(new CoinAdapter.OnBtnClickListener() {
            @Override
            public void onClick(int pos, boolean isSelected) {
                MyApplication.getApp().setChangeWallet(true);
                MyCoin myCoin = recList.get(pos);
                ContentValues values = new ContentValues();
                values.put("isAdded", isSelected);
                DataSupport.updateAll(MyCoin.class, values, "walletName = ? and name = ?", myCoin.getWalletName(), myCoin.getName());
            }
        });
    }

    @Override
    protected void obtainData() {
        if (MyApplication.getApp().getCurrentWallet() != null) {
            List<MyCoin> list = DataSupport.where("walletName = ?",
                    MyApplication.getApp().getCurrentWallet().getName()).find(MyCoin.class);

            if (list != null) {
                myCoinList.clear();
                recList.clear();

                for (int i = 0; i < list.size(); i++) {
                    if (StringUtils.isEmpty(list.get(i).getContractAddress())) {
                        myCoinList.add(list.get(i));
                    } else {
                        recList.add(list.get(i));
                    }
                }

                if (recList.size() > 0) {
                    tvERC.setVisibility(View.VISIBLE);
                } else {
                    tvERC.setVisibility(View.GONE);
                }

                sort(myCoinList);
                adapter.notifyDataSetChanged();
                recAdapter.notifyDataSetChanged();
            }
        }
    }

    public void sort(List<MyCoin> myCoinList) {
        //根据币种order进行排序，代币没有order值，放在最后。返回正整数位置排在后，返回负整数位置排在前，返回0位置不变
        Collections.sort(myCoinList, new Comparator<MyCoin>() {
            @Override
            public int compare(MyCoin lhs, MyCoin rhs) {
                if (CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()) != null &&
                        CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()) != null) {
                    return CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()).getOrder() - CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()).getOrder();
                } else if (CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()) != null) {
                    return -1;
                } else if (CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()) != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }


}
