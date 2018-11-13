package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.LinkMan;

import java.util.List;

public class LinkManAdapter extends BaseQuickAdapter<LinkMan, BaseViewHolder> {
    private Context context;

    public LinkManAdapter(Context context, int layoutResId, @Nullable List<LinkMan> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, LinkMan item) {
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvCircle, item.getInitial());

//        List<AddressBean> addressBeanList = DataSupport.where("linkManName = ?", item.getName()).find(AddressBean.class);
//        if (addressBeanList != null && addressBeanList.size() > 0) {
//            helper.setText(R.id.tvAddress, addressBeanList.get(0).getAddress());
//            helper.setText(R.id.tvNum, addressBeanList.size() + "");
//        }
    }


}
