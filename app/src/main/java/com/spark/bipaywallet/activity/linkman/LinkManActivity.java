package com.spark.bipaywallet.activity.linkman;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.createman.CreateManActivity;
import com.spark.bipaywallet.activity.editman.EditManActivity;
import com.spark.bipaywallet.adapter.LinkManAdapter;
import com.spark.bipaywallet.base.BaseActivity;
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
 * 联系人
 */

public class LinkManActivity extends BaseActivity {
    @BindView(R.id.rvMan)
    RecyclerView rvMan;
    @BindView(R.id.tvAdd)
    TextView tvAdd;
    @BindView(R.id.indexBar)
    IndexBar indexBar;
    @BindView(R.id.tvSideBarHint)
    TextView tvSideBarHint;

    private LinkManAdapter adapter;
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
            adapter.notifyDataSetChanged();

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
            adapter.notifyDataSetChanged();
            titleList.clear();
            indexBar.setmSourceDatas(titleList);
            titleItemDecoration.setmDatas(titleList);
        }
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_linkman;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_man_title));
        initRv();
    }

    @OnClick(R.id.tvAdd)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        showActivity(CreateManActivity.class, null);
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvMan.setLayoutManager(manager);
        linkManList = DataSupport.findAll(LinkMan.class);
        if (linkManList == null) {
            linkManList = new ArrayList<>();
        }
        sort(linkManList);
        adapter = new LinkManAdapter(this, R.layout.item_linkman, linkManList);
        titleList = new ArrayList<>();
        for (int i = 0; i < linkManList.size(); i++) {
            if (Character.isLetter(linkManList.get(i).getInitial().charAt(0))) {
                titleList.add(linkManList.get(i).getInitial());
            } else {
                titleList.add("#");
            }
        }
        rvMan.setAdapter(adapter);
        rvMan.addItemDecoration(titleItemDecoration = new TitleItemDecoration(this, titleList));
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_linkman, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(emptyView);
        //使用indexBar
        indexBar.setmPressedShowTextView(tvSideBarHint)//设置HintTextView
                .setNeedRealIndex(false)//设置需要真实的索引
                .setmLayoutManager(manager)//设置RecyclerView的LayoutManager
                .setmSourceDatas(titleList);//设置数据源
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("linkMan", linkManList.get(position));
                showActivity(EditManActivity.class, bundle);
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
