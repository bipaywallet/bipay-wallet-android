package com.spark.bipaywallet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.utils.CommonUtils;

public class ConfirmPayDialog extends Dialog {

    private ClickListenerInterface clickListenerInterface;

    private TextView tvConfirm;
    private String money;
    private String address;
    private String fee;
    private String remark;
    private Context context;

    public interface ClickListenerInterface {

        void doConfirm();

        void doCancel();
    }

    public ConfirmPayDialog(Context context, String money, String address, String fee, String remark) {
        super(context, R.style.dialog);
        this.money = money;
        this.address = address;
        this.fee = fee;
        this.remark = remark;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_confirm_pay);

        ImageView tvCancel = findViewById(R.id.ivCancel);
        tvConfirm = findViewById(R.id.tvConfirm);

        TextView tvMoney = findViewById(R.id.tvMoney);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvFee = findViewById(R.id.tvFee);
        TextView tvRemark = findViewById(R.id.tvRemark);
        if (money != null) {
            tvMoney.setText(money);
        }
        if (address != null) {
            tvAddress.setText(address);
        }
        if (fee != null) {
            tvFee.setText(fee);
        }
        if (remark != null) {
            tvRemark.setText(remark);
        }
        tvCancel.setOnClickListener(new clickListener());
        tvConfirm.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = CommonUtils.getScreenWidth();
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvConfirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.ivCancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }


}
