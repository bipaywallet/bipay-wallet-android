package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.AddressBean;

import java.util.List;

public class WalletAddressAdapter extends BaseQuickAdapter<AddressBean, BaseViewHolder> {
    private Context context;

    private int selPos = -1;
    private int oldPos = 0;

    public WalletAddressAdapter(Context context, int layoutResId, @Nullable List<AddressBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressBean item) {
        ImageView ivRight = helper.getView(R.id.ivRight);
        if (helper.getAdapterPosition() == selPos) {
            ivRight.setSelected(true);
        } else {
            ivRight.setSelected(false);
        }

        helper.setText(R.id.tvAddress, item.getAddress())
                .setText(R.id.tvCoinName, item.getCoinName());
    }

    public int getSelPos() {
        return selPos;
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
        notifyItemChanged(oldPos);
        notifyItemChanged(selPos);
        oldPos = selPos;
    }

}
