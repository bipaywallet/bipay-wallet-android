package com.spark.bipaywallet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.utils.CommonUtils;


/**
 * 从中间弹出
 */
public class WhiteCenterDialog extends Dialog {

    private Context context;

    public WhiteCenterDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (CommonUtils.getScreenWidth() * 0.8);
        dialogWindow.setAttributes(lp);
    }


}
