package com.spark.bipaywallet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.utils.CommonUtils;

public class ShareDialog extends Dialog {

    private Context context;

    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {

        void doWeixin();

        void doPyq();

        void doCancel();
    }

    public ShareDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_share);

        LinearLayout llWeixin = findViewById(R.id.llWeixin);
        LinearLayout llPyq = findViewById(R.id.llPyq);
        TextView tvCancel = findViewById(R.id.tvCancel);

        llWeixin.setOnClickListener(new clickListener());
        llPyq.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

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
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.llWeixin:
                    clickListenerInterface.doWeixin();
                    break;
                case R.id.llPyq:
                    clickListenerInterface.doPyq();
                    break;
                case R.id.tvCancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }


}
