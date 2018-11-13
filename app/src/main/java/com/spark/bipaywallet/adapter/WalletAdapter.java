package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.ManageWallet;
import com.spark.bipaywallet.entity.MyCoin;

import org.litepal.crud.DataSupport;

import java.util.List;

public class WalletAdapter extends BaseQuickAdapter<ManageWallet, BaseViewHolder> {
    private Context context;
//    private int selectPos = -1;

    public WalletAdapter(Context context, int layoutResId, @Nullable List<ManageWallet> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ManageWallet item) {
        helper.setText(R.id.tvName, item.getName());

        List<MyCoin> list = DataSupport.where("walletName = ? and isAdded = ?",
                item.getName(), "1").find(MyCoin.class);
        if (list != null) {
            helper.setText(R.id.tvCoinNum, context.getString(R.string.wallet_adapter_have) + list.size() + context.getString(R.string.wallet_adapter_currencies));
        }

        helper.addOnClickListener(R.id.flMore);

        LinearLayout llAdapterBg = helper.getView(R.id.llAdapterBg);
        if (helper.getAdapterPosition() == 0) {
            llAdapterBg.setSelected(true);
            helper.setTextColor(R.id.tvCoinNum, context.getResources().getColor(R.color.white));
//            helper.setTextColor(R.id.tvMoney, context.getResources().getColor(R.color.black));
        } else {
            llAdapterBg.setSelected(false);
            helper.setTextColor(R.id.tvCoinNum, context.getResources().getColor(R.color.font_sec_grey));
//            helper.setTextColor(R.id.tvMoney, context.getResources().getColor(R.color.font_main_green));
        }
    }

//    public void setSelectPos(int pos) {
//        int oldPos = selectPos;
//        this.selectPos = pos;
//        notifyItemChanged(oldPos);
//        notifyItemChanged(selectPos);
//    }


}
