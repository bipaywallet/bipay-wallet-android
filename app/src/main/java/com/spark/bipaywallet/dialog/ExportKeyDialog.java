package com.spark.bipaywallet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.ToastUtils;

public class ExportKeyDialog extends Dialog {

    private ClickListenerInterface clickListenerInterface;

    private TextView tvCopy;
    private String key;
    private String title;
    private Context context;

    public interface ClickListenerInterface {

        void doCopy();

        void doCancel();
    }

    public ExportKeyDialog(Context context, String key, String title) {
        super(context, R.style.dialog);
        this.key = key;
        this.title = title;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_export_private_key);

        ImageView tvCancel = findViewById(R.id.ivCancel);
        tvCopy = findViewById(R.id.tvCopy);

        TextView tvKey = findViewById(R.id.tvKey);
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (key != null) {
            tvKey.setText(key);
        }
        if (title != null) {
            tvTitle.setText(title);
        }

        tvCancel.setOnClickListener(new clickListener());
        tvCopy.setOnClickListener(new clickListener());

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
                case R.id.tvCopy:
                    clickListenerInterface.doCopy();
                    tvCopy.setText(context.getString(R.string.dialog_copy_over));
                    tvCopy.setBackgroundColor(Color.parseColor("#C6C6C6"));
                    break;
                case R.id.ivCancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }


}
