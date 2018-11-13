package com.spark.bipaywallet.activity.showlinkman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.createman.CreateManActivity;
import com.spark.bipaywallet.activity.transferpay.TransferPayActivity;
import com.spark.bipaywallet.adapter.ShowLinkManAdapter;
import com.spark.bipaywallet.adapter.WalletAddressAdapter;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.dialog.WhiteCenterDialog;
import com.spark.bipaywallet.entity.AddressBean;
import com.spark.bipaywallet.entity.LinkMan;
import com.spark.bipaywallet.ui.IndexBar;
import com.spark.bipaywallet.ui.TitleItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 转账界面进入的联系人界面
 */

public class ShowLinkManActivity extends BaseActivity {
    @BindView(R.id.rvMan)
    RecyclerView rvMan;
    @BindView(R.id.indexBar)
    IndexBar indexBar;
    @BindView(R.id.tvSideBarHint)
    TextView tvSideBarHint;
    @BindView(R.id.tvAdd)
    TextView tvAdd;

    private ShowLinkManAdapter showLinkManAdapter;
    private List<LinkMan> linkManList;
    private List<String> titleList;
    private TitleItemDecoration titleItemDecoration;

    @Override
    protected void onRestart() {
        super.onRestart();

        List<LinkMan> ll = DataSupport.findAll(LinkMan.class);
        if (ll != null) {
            linkManList.clear();
            linkManList.addAll(ll);
            sort(linkManList);
            showLinkManAdapter.notifyDataSetChanged();

            titleList.clear();
            for (int i = 0; i < linkManList.size(); i++) {
                if (Character.isLetter(linkManList.get(i).getInitial().charAt(0))) {
                    titleList.add(linkManList.get(i).getInitial());
                } else {
                    titleList.add("#");
                }
            }
            indexBar.setmSourceDatas(titleList);
            titleItemDecoration.setmDatas(titleList);
        } else {
            linkManList.clear();
            showLinkManAdapter.notifyDataSetChanged();
            titleList.clear();
            indexBar.setmSourceDatas(titleList);
            titleItemDecoration.setmDatas(titleList);
        }
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_showlinkman;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        initRv();
    }

    @OnClick(R.id.tvAdd)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        showActivity(CreateManActivity.class, null);
    }

    private void initRv() {
        setTitle(getString(R.string.activity_man_title));
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvMan.setLayoutManager(manager);

        linkManList = DataSupport.findAll(LinkMan.class);
        if (linkManList == null) {
            linkManList = new ArrayList<>();
        }
        sort(linkManList);
        titleList = new ArrayList<>();
        for (int i = 0; i < linkManList.size(); i++) {
            if (Character.isLetter(linkManList.get(i).getInitial().charAt(0))) {
                titleList.add(linkManList.get(i).getInitial());
            } else {
                titleList.add("#");
            }
        }
        showLinkManAdapter = new ShowLinkManAdapter(this, R.layout.adapter_showlinkman, linkManList);

        rvMan.setAdapter(showLinkManAdapter);
        rvMan.addItemDecoration(titleItemDecoration = new TitleItemDecoration(this, titleList));
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_linkman, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        showLinkManAdapter.setEmptyView(emptyView);

        //使用indexBar
        indexBar.setmPressedShowTextView(tvSideBarHint)//设置HintTextView
                .setNeedRealIndex(false)//设置需要真实的索引
                .setmLayoutManager(manager)//设置RecyclerView的LayoutManager
                .setmSourceDatas(titleList);//设置数据源
    }

    @Override
    protected void setListener() {
        super.setListener();
        showLinkManAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                showLinkManAdapter.setSelPos(position);
                showAddressDialog(linkManList.get(position).getName());
            }
        });
    }

    private void showAddressDialog(String name) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_wallet_address, null);
        final WhiteCenterDialog whiteCenterDialog = new WhiteCenterDialog(this);
        whiteCenterDialog.setContentView(contentView);
        whiteCenterDialog.show();

        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);

        RecyclerView rv = contentView.findViewById(R.id.rvAddress);
        rv.setLayoutManager(manager);

        final List<AddressBean> addressBeanList = DataSupport.where("linkManName = ?", name).find(AddressBean.class);

        final WalletAddressAdapter addressAdapter = new WalletAddressAdapter(this, R.layout.adapter_wallet_address, addressBeanList);
        addressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                addressAdapter.setSelPos(position);
            }
        });
        rv.setAdapter(addressAdapter);

        TextView tvName = contentView.findViewById(R.id.tvName);
        tvName.setText(name);

        TextView tvCancel = contentView.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteCenterDialog.dismiss();
            }
        });

        TextView tvConfirm = contentView.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressAdapter.getSelPos() >= 0) {
                    whiteCenterDialog.dismiss();

                    Intent intent = new Intent();
                    intent.putExtra("addressStr", addressBeanList.get(addressAdapter.getSelPos()).getAddress());
                    setResult(TransferPayActivity.SELECT_LINKMAN, intent);
                    finish();
                }
            }
        });
    }


    //首字母排序
    public void sort(List<LinkMan> linkManList) {
        // 排序
        Collections.sort(linkManList, new Comparator<LinkMan>() {
            @Override
            public int compare(LinkMan lhs, LinkMan rhs) {
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
