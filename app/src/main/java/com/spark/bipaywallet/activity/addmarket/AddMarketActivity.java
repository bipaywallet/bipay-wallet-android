package com.spark.bipaywallet.activity.addmarket;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.AddMarketAdapter;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.MyCurrencyData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 添加行情
 */

public class AddMarketActivity extends BaseActivity {
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.rvOKEX)
    RecyclerView rvOKEX;

    private AddMarketAdapter adapter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddMarketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_addmarket;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvOKEX.setLayoutManager(manager);

        List<MyCurrencyData> list = new ArrayList<>();
        MyCurrencyData c = new MyCurrencyData();
        list.add(c);
        list.add(c);
        list.add(c);
        list.add(c);
        list.add(c);
        list.add(c);

        adapter = new AddMarketAdapter(this, R.layout.adapter_addmarket, list);
        adapter.setOnBtnClickListener(new AddMarketAdapter.OnBtnClickListener() {
            @Override
            public void onClick(int pos, boolean isSelected) {
            }
        });
        rvOKEX.setAdapter(adapter);

        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_message, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //添加空视图
        adapter.setEmptyView(emptyView);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
