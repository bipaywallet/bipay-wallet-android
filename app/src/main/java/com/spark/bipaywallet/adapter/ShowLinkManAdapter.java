package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.AddressBean;
import com.spark.bipaywallet.entity.LinkMan;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ShowLinkManAdapter extends BaseQuickAdapter<LinkMan, BaseViewHolder> {
    private Context context;

//    private int selPos = -1;
//    private int oldPos = 0;

    public ShowLinkManAdapter(Context context, int layoutResId, @Nullable List<LinkMan> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, LinkMan item) {
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvCircle, item.getInitial());

        List<AddressBean> addressBeanList = DataSupport.where("linkManName = ?", item.getName()).find(AddressBean.class);
        if (addressBeanList != null && addressBeanList.size() > 0) {
            helper.setText(R.id.tvAddress, addressBeanList.get(0).getAddress())
                    .setText(R.id.tvCoinName, addressBeanList.get(0).getCoinName())
                    .setText(R.id.tvNum, addressBeanList.size() + "");
        }


//        ImageView ivRight = helper.getView(R.id.ivRight);
//        if (helper.getAdapterPosition() == selPos) {
//            ivRight.setSelected(true);
//        } else {
//            ivRight.setSelected(false);
//        }
    }

//    public int getSelPos() {
//        return selPos;
//    }
//
//    public void setSelPos(int selPos) {
//        this.selPos = selPos;
//        notifyItemChanged(oldPos);
//        notifyItemChanged(selPos);
//        oldPos = selPos;
//    }


}
