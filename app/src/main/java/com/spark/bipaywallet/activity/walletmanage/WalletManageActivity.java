package com.spark.bipaywallet.activity.walletmanage;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.createwallet.CreateWalletActivity;
import com.spark.bipaywallet.activity.importwallet.ImportWalletActivity;
import com.spark.bipaywallet.activity.walletdetails.WalletDetailsActivity;
import com.spark.bipaywallet.adapter.WalletAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.ManageWallet;
import com.spark.bipaywallet.entity.Wallet;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包管理
 */

public class WalletManageActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvWallet)
    RecyclerView rvWallet;
    @BindView(R.id.llCreateWallet)
    LinearLayout llCreateWallet;
    @BindView(R.id.llLeadIn)
    LinearLayout llLeadIn;
    private WalletAdapter walletAdapter;
    private List<ManageWallet> manageWalletList;
    private List<Wallet> walletList;


    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData();
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_wallet_manage;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_four_wallet));
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvWallet.setLayoutManager(manager);
        walletList = DataSupport.findAll(Wallet.class);
        manageWalletList = new ArrayList<>();
        if (walletList != null && walletList.size() > 0) {
            for (int i = 0; i < walletList.size(); i++) {
                ManageWallet manageWallet = new ManageWallet();
                manageWallet.setName(walletList.get(i).getName());
                int index = -1;
                if (MyApplication.getApp().getCurrentWallet() != null) {
                    for (int k = 0; k < walletList.size(); k++) {
                        if (walletList.get(k).getName().equals(MyApplication.getApp().getCurrentWallet().getName())) {
                            index = k;
                            break;
                        }
                    }
                }
                if (i == index) {
                    manageWallet.setInitial("a");
                } else {
                    manageWallet.setInitial("b");
                }
//                manageWallet.setAllMoney();
                manageWalletList.add(manageWallet);
            }
            sort(manageWalletList);
        }
        walletAdapter = new WalletAdapter(this, R.layout.item_wallet, manageWalletList);
        rvWallet.setAdapter(walletAdapter);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_wallet, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((TextView) emptyView.findViewById(R.id.tvMessage)).setText(getString(R.string.empty_no_wallet_data_title));
        //添加空视图
        walletAdapter.setEmptyView(emptyView);

//        if (walletList != null && walletList.size() > 0) {
//            walletAdapter.setSelectPos(SharedPreferenceInstance.getInstance().getCurrentWalletPos());
//        } else {
//            walletAdapter.setSelectPos(-1);
//        }
    }

    @OnClick({R.id.llCreateWallet, R.id.llLeadIn})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llLeadIn:
                showActivity(ImportWalletActivity.class, null);
                break;
            case R.id.llCreateWallet:
                showActivity(CreateWalletActivity.class, null);
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        walletAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                walletAdapter.setSelectPos(position);
                if (manageWalletList != null && manageWalletList.size() > position) {
                    List<Wallet> walletList = DataSupport.where("name = ?", manageWalletList.get(position).getName()).find(Wallet.class);
                    MyApplication.getApp().setCurrentWallet(walletList.get(0));
//                    SharedPreferenceInstance.getInstance().setCurrentWalletPos(position);
                }
                finish();
            }
        });
        walletAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.flMore:
                        Bundle bundle = new Bundle();
                        bundle.putString("walletName", manageWalletList.get(position).getName());
                        showActivity(WalletDetailsActivity.class, bundle);
                        break;
                }
            }
        });
    }

    private void refreshData() {
        if (manageWalletList != null) {
            manageWalletList.clear();
            walletList = DataSupport.findAll(Wallet.class);
            if (walletList != null && walletList.size() > 0) {
                for (int i = 0; i < walletList.size(); i++) {
                    ManageWallet manageWallet = new ManageWallet();
                    manageWallet.setName(walletList.get(i).getName());
                    int index = -1;
                    if (MyApplication.getApp().getCurrentWallet() != null) {
                        for (int k = 0; k < walletList.size(); k++) {
                            if (walletList.get(k).getName().equals(MyApplication.getApp().getCurrentWallet().getName())) {
                                index = k;
                                break;
                            }
                        }
                    }
                    if (i == index) {
                        manageWallet.setInitial("a");
                    } else {
                        manageWallet.setInitial("b");
                    }
//                    manageWallet.setAllMoney();

                    manageWalletList.add(manageWallet);
                }
                sort(manageWalletList);
            }
            walletAdapter.notifyDataSetChanged();

//            if (walletList != null && walletList.size() > 0) {
//                walletAdapter.setSelectPos(SharedPreferenceInstance.getInstance().getCurrentWalletPos());
//            } else {
//                walletAdapter.setSelectPos(-1);
//            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    //首字母排序
    public void sort(List<ManageWallet> manageWalletList) {
        // 排序
        Collections.sort(manageWalletList, new Comparator<ManageWallet>() {
            @Override
            public int compare(ManageWallet lhs, ManageWallet rhs) {
                if (lhs.getInitial().equals(rhs.getInitial())) {
                    return lhs.getName().compareTo(rhs.getName());
                } else {
                    if (!Character.isLetter(lhs.getInitial().charAt(0))) {
                        return 1;
                    } else if (!Character.isLetter(rhs.getInitial().charAt(0))) {
                        return -1;
                    }
                    return lhs.getInitial().compareTo(rhs.getInitial());
                }
            }
        });
    }


}
