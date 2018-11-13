package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class HomeWalletAdapter extends BaseQuickAdapter<MyCoin, BaseViewHolder> {
    private Context context;
    private boolean isShow = true;

    public HomeWalletAdapter(Context context, int layoutResId, @Nullable List<MyCoin> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyCoin item) {
        int moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();

        if (StringUtils.isNotEmpty(item.getName())) {
            helper.setText(R.id.tvName, item.getName());
        } else {
            helper.setText(R.id.tvName, "---");
        }

        if (StringUtils.isNotEmpty(item.getName()) && CoinTypeEnum.getCoinTypeEnumByName(item.getName()) != null) {
            helper.setImageResource(R.id.ivIcon, CoinTypeEnum.getCoinTypeEnumByName(item.getName()).getResId());
        } else {
            if (StringUtils.isNotEmpty(item.getLogoUrl()) && !item.getLogoUrl().equals("NULL")) {
                Glide.with(context).load(item.getLogoUrl()).into((ImageView) helper.getView(R.id.ivIcon));
            } else {
                helper.setImageResource(R.id.ivIcon, R.mipmap.token);
            }
        }

        if (moneyCode == 1) {
            helper.setText(R.id.tvExchange, "≈" + MathUtils.getBigDecimalRundNumber(item.getRate(), 2) + " CNY");
        } else {
            helper.setText(R.id.tvExchange, "≈" + MathUtils.getBigDecimalRundNumber(item.getUsdRate(), 2) + " USD");
        }

        if (isShow) {
            helper.setText(R.id.tvCoin, MathUtils.getBigDecimalRundNumber(item.getNum(), 8));

            if (moneyCode == 1) {
                helper.setText(R.id.tvMoney, "≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(item.getNum(), item.getRate(), 8), 2) + " CNY");
            } else {
                helper.setText(R.id.tvMoney, "≈" + MathUtils.getBigDecimalRundNumber(MathUtils.getBigDecimalMultiply(item.getNum(), item.getUsdRate(), 8), 2) + " USD");
            }
        } else {
            helper.setText(R.id.tvCoin, "****")
                    .setText(R.id.tvMoney, "****");
        }

        if (StringUtils.isNotEmpty(item.getName()) && CoinTypeEnum.getCoinTypeEnumByName(item.getName()) == null) {
            helper.getView(R.id.tvERC).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tvERC).setVisibility(View.GONE);
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


}
