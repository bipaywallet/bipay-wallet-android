package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyCurrencyData;

import java.util.List;

public class MarketAdapter extends BaseQuickAdapter<MyCurrencyData, BaseViewHolder> {
    private Context context;
    private String marketName;

    public MarketAdapter(Context context, int layoutResId, @Nullable List<MyCurrencyData> data, String marketName) {
        super(layoutResId, data);
        this.context = context;
        this.marketName = marketName;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyCurrencyData item) {
        if (marketName == null) return;

        helper.setText(R.id.tvCoinName, item.getName().toUpperCase())
                .setText(R.id.tvMarketName, marketName)
                .setText(R.id.tvRMB, "¥" + item.getClose_rmb())
                .setText(R.id.tvUS, "$" + item.getClose())
                .setText(R.id.tvRise, item.getRise() + "%")
                .setBackgroundRes(R.id.tvRise, Double.valueOf(item.getRise()) >= 0 ? R.drawable.shape_bg_green : R.drawable.shape_bg_red);
    }
}
