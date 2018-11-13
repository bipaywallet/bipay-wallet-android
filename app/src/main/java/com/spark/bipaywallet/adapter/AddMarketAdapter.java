package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyCurrencyData;

import java.util.List;

public class AddMarketAdapter extends BaseQuickAdapter<MyCurrencyData, BaseViewHolder> {
    private Context context;

    private OnBtnClickListener onBtnClickListener;

    public AddMarketAdapter(Context context, int layoutResId, @Nullable List<MyCurrencyData> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MyCurrencyData item) {
        final ImageView ivSwitchBtn = helper.getView(R.id.ivSwitchBtn);
        ivSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBtnClickListener != null) {
                    ivSwitchBtn.setSelected(!ivSwitchBtn.isSelected());
                    onBtnClickListener.onClick(helper.getAdapterPosition(), ivSwitchBtn.isSelected());
                }
            }
        });
    }

    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    public interface OnBtnClickListener {
        void onClick(int pos, boolean isSelected);
    }
}
