package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class ExchangeRecordAdapter extends BaseQuickAdapter<MyExchangeRecord, BaseViewHolder> {
    private Context context;

    public ExchangeRecordAdapter(Context context, int layoutResId, @Nullable List<MyExchangeRecord> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MyExchangeRecord item) {
        if (StringUtils.isNotEmpty(item.getCurrencyFrom(), item.getAmountExpectedFrom())) {
            helper.setText(R.id.tvFrom, item.getCurrencyFrom() + " " + item.getAmountExpectedFrom());
        }

        if (StringUtils.isNotEmpty(item.getCurrencyTo(), item.getAmountExpectedTo())) {
            helper.setText(R.id.tvTo, item.getCurrencyTo() + " " + item.getAmountExpectedTo());
        }

        if (StringUtils.isNotEmpty(item.getTime())) {
            helper.setText(R.id.tvTime, item.getTime());
        }

        if (StringUtils.isNotEmpty(item.getPayoutAddress())) {
            helper.setText(R.id.tvFromAddress, item.getPayoutAddress());
        }

        if (StringUtils.isNotEmpty(item.getPayinAddress())) {
            helper.setText(R.id.tvToAddress, item.getPayinAddress());
        }

        if (StringUtils.isNotEmpty(item.getRate())) {
            helper.setText(R.id.tvRate, item.getRate());
        }

        if (StringUtils.isNotEmpty(item.getStatus())) {
            helper.setText(R.id.tvStatus, getStatusStr(item.getStatus()))
                    .setTextColor(R.id.tvStatus, item.getStatus().equals("finished") ? context.getResources().getColor(R.color.btn_option_normal) :
                            context.getResources().getColor(R.color.txtRed));
        }
    }

    private String getStatusStr(String status) {
        switch (status) {
            case "waiting":
                return context.getString(R.string.exchange_confirming);
            case "confirming":
                return context.getString(R.string.exchange_confirming);
            case "exchanging":
                return context.getString(R.string.exchange_exchanging);
            case "sending":
                return context.getString(R.string.exchange_confirming);
            case "finished":
                return context.getString(R.string.exchange_finished);
            default:
                return context.getString(R.string.exchange_fail);
        }
    }

}
