package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class NameAdapter extends BaseQuickAdapter<MyCoin, BaseViewHolder> {
    private Context context;

    public NameAdapter(Context context, int layoutResId, @Nullable List<MyCoin> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyCoin item) {
        if (StringUtils.isNotEmpty(item.getName())) {
            helper.setText(R.id.tvName, item.getName() + " " + context.getResources().getString(R.string.activity_wallet_details_key));
        } else {
            helper.setText(R.id.tvName, "--- " + context.getResources().getString(R.string.activity_wallet_details_key));
        }
    }
}
