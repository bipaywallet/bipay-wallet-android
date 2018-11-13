package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.Notice;

import java.util.List;

public class HelpAdapter extends BaseQuickAdapter<Notice, BaseViewHolder> {
    private Context context;

    public HelpAdapter(Context context, int layoutResId, @Nullable List<Notice> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Notice item) {
        helper.setText(R.id.tvName, item.getTitle());
    }
}
