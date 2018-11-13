package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class CoinAdapter extends BaseQuickAdapter<MyCoin, BaseViewHolder> {
    private Context context;

    private OnBtnClickListener onBtnClickListener;

    public CoinAdapter(Context context, int layoutResId, @Nullable List<MyCoin> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MyCoin item) {
        if (StringUtils.isNotEmpty(item.getName())) {
            helper.setText(R.id.tvName, item.getName());
        }else {
            helper.setText(R.id.tvName, "---");
        }

        final ImageView ivSwitchBtn = helper.getView(R.id.ivSwitchBtn);
        ivSwitchBtn.setSelected(item.isAdded());
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
