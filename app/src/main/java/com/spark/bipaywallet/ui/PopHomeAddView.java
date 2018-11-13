package com.spark.bipaywallet.ui;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.spark.bipaywallet.R;

public class PopHomeAddView extends PopupWindow {
    private LinearLayout llScan; // 扫一扫
    private LinearLayout llCreateWallet; // 创建钱包
    private LinearLayout llLeadIn; // 导入钱包
    private LinearLayout llAddMoney; // 添加资产
    private View mainView;

    public PopHomeAddView(Activity paramActivity, View.OnClickListener paramOnClickListener, int width, int height) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.pop_home_add, null);
        setContentView(mainView);

        llScan = mainView.findViewById(R.id.llScan);
        llCreateWallet = mainView.findViewById(R.id.llCreateWallet);
        llLeadIn = mainView.findViewById(R.id.llLeadIn);
        llAddMoney = mainView.findViewById(R.id.llAddMoney);

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llScan.setOnClickListener(paramOnClickListener);
            llCreateWallet.setOnClickListener(paramOnClickListener);
            llLeadIn.setOnClickListener(paramOnClickListener);
            llAddMoney.setOnClickListener(paramOnClickListener);
        }
        //设置宽度
        setWidth(width);
        //设置高度
        setHeight(height);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

}
