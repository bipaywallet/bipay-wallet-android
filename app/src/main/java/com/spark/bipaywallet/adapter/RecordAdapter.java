package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.List;

public class RecordAdapter extends BaseQuickAdapter<MyRecordBean, BaseViewHolder> {
    private Context context;
//    private String searchWalletName;
//    private int coinType;

    public RecordAdapter(Context context, int layoutResId, @Nullable List<MyRecordBean> data) {
        super(layoutResId, data);
        this.context = context;
//        this.coinType = coinType;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyRecordBean item) {
        if (StringUtils.isNotEmpty(item.getTxid())) {
            helper.setText(R.id.tvAddress, item.getTxid());
        }

        helper.setImageResource(R.id.ivIcon, item.isOut() ? R.mipmap.icon_pay : R.mipmap.icon_receivables)
                .setText(R.id.tvTrade, item.isOut() ? context.getResources().getString(R.string.activity_payment_zhuanzhang) :
                        context.getResources().getString(R.string.activity_payment_shoukuan))
                .setText(R.id.tvCoin, item.isOut() ? "-" + MathUtils.getBigDecimalRundNumber(item.getValue(), 8) :
                        "+" + MathUtils.getBigDecimalRundNumber(item.getValue(), 8))
                .setText(R.id.tvTime, item.getTime())
                .setText(R.id.tvStatus, item.getBlockHeight() == 0 ? context.getResources().getString(R.string.record_wait_confirm) :
                        context.getResources().getString(R.string.record_finish))
                .setTextColor(R.id.tvStatus, item.getBlockHeight() == 0 ? context.getResources().getColor(R.color.txtRed) :
                        context.getResources().getColor(R.color.font_sec_grey));
    }

    //    @Override
//    protected void convert(BaseViewHolder helper, MyRecordBean item) {
//        if (searchWalletName == null) return;
//
//        if (coinType == 0 || coinType == 206) {
//            MyDVCRecord.NormalTransaction normalTransaction = (MyDVCRecord.NormalTransaction) item;
//
//            boolean isOut = false;//是转出
//            for (int i = 0; i < normalTransaction.getInputs().size(); i++) {
//                List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                        searchWalletName, normalTransaction.getInputs().get(i).getAddress()).find(MyCoin.class);
//
//                if (myCoinList != null && myCoinList.size() > 0) {
//                    isOut = true;
//                    break;
//                }
//            }
//            helper.setImageResource(R.id.ivIcon, isOut ? R.mipmap.icon_record_on : R.mipmap.icon_record_finish);
//            helper.setText(R.id.tvTime, normalTransaction.getTime());
//            helper.setText(R.id.tvAddress, normalTransaction.getTxid());
//
//            if (isOut) {
//                boolean isSet = false;
//                for (int i = 0; i < normalTransaction.getOutputs().size(); i++) {
//                    List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                            searchWalletName, normalTransaction.getOutputs().get(i).getAddress()).find(MyCoin.class);
//
//                    if (myCoinList == null || myCoinList.size() == 0) {
//                        helper.setText(R.id.tvCoin, "-" + WonderfulMathUtils.getRundFormat(normalTransaction.getOutputs().get(i).getAmount()));
////                        helper.setText(R.id.tvAddress, normalTransaction.getOutputs().get(i).getAddress());
//                        isSet = true;
//                        break;
//                    }
//                }
//
//                if (!isSet) {
//                    helper.setText(R.id.tvCoin, "-0.0");
////                    helper.setText(R.id.tvAddress, normalTransaction.getOutputs().get(0).getAddress());
//                }
//            } else {
//                for (int i = 0; i < normalTransaction.getOutputs().size(); i++) {
//                    List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                            searchWalletName, normalTransaction.getOutputs().get(i).getAddress()).find(MyCoin.class);
//
//                    if (myCoinList != null && myCoinList.size() > 0) {
//                        helper.setText(R.id.tvCoin, "+" + WonderfulMathUtils.getRundFormat(normalTransaction.getOutputs().get(i).getAmount()));
//                        break;
//                    }
//                }
////                helper.setText(R.id.tvAddress, normalTransaction.getInputs().get(0).getAddress());
//            }
//        } else if (coinType == 60) {
//            MyETHRecord.ETHNormalTransaction normalTransaction = (MyETHRecord.ETHNormalTransaction) item;
//
//            boolean isOut = false;//是转出
//
//            List<MyCoin> myCoinList = DataSupport.where("walletName = ? and address = ?",
//                    searchWalletName, normalTransaction.getFrom()).find(MyCoin.class);
//
//            if (myCoinList != null && myCoinList.size() > 0) {
//                isOut = true;
//            }
//
//            helper.setImageResource(R.id.ivIcon, isOut ? R.mipmap.icon_record_on : R.mipmap.icon_record_finish);
//            helper.setText(R.id.tvTime, normalTransaction.getTime());
//
//            if (isOut) {
//                helper.setText(R.id.tvCoin, "-" + WonderfulMathUtils.getRundFormat(normalTransaction.getValue()));
////                helper.setText(R.id.tvAddress, normalTransaction.getTo());
//            } else {
//                helper.setText(R.id.tvCoin, "+" + WonderfulMathUtils.getRundFormat(normalTransaction.getValue()));
////                helper.setText(R.id.tvAddress, normalTransaction.getFrom());
//            }
//        }
//    }

//    public String getSearchWalletName() {
//        return searchWalletName;
//    }
//
//    public void setSearchWalletName(String searchWalletName) {
//        this.searchWalletName = searchWalletName;
//    }
}
