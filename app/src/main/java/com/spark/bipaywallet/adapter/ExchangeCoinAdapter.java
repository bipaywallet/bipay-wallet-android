package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class ExchangeCoinAdapter extends BaseQuickAdapter<MyCoin, BaseViewHolder> {
    private Context context;
    private OnBtnClickListener onBtnClickListener;
    private int pos = -1;

    public ExchangeCoinAdapter(Context context, int layoutResId, @Nullable List<MyCoin> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MyCoin item) {
        if (StringUtils.isNotEmpty(item.getName())) {
            helper.setText(R.id.tvCoinName, item.getName());
        } else {
            helper.setText(R.id.tvCoinName, "---");
        }

        if (StringUtils.isNotEmpty(item.getName()) && CoinTypeEnum.getCoinTypeEnumByName(item.getName()) != null) {
            helper.setImageResource(R.id.ivIcon, CoinTypeEnum.getCoinTypeEnumByName(item.getName()).getResId());
            helper.setText(R.id.tvFullName, CoinTypeEnum.getCoinTypeEnumByName(item.getName()).getFullName());
        }

        helper.getView(R.id.llRoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.onClick(helper.getAdapterPosition());
                }
            }
        });

        ImageView ivRight = helper.getView(R.id.ivRight);
        if (helper.getAdapterPosition() == pos) {
            ivRight.setVisibility(View.VISIBLE);
        } else {
            ivRight.setVisibility(View.GONE);
        }
    }

    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    public interface OnBtnClickListener {
        void onClick(int pos);
    }

    public void setPos(int position) {
        int oldPos = pos;
        this.pos = position;
        notifyItemChanged(oldPos);
        notifyItemChanged(pos);
    }
}
