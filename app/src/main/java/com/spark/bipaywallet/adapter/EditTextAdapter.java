package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.AddressBean;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EditTextAdapter extends BaseQuickAdapter<AddressBean, BaseViewHolder> {
    private Context context;
    private OnDelClickListener onDelClickListener;

    public EditTextAdapter(Context context, int layoutResId, @Nullable List<AddressBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final AddressBean item) {
        TextView tvCoinType = helper.getView(R.id.tvCoinType);
        tvCoinType.setText(item.getCoinName());
        tvCoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelClickListener.select(helper.getAdapterPosition());
            }
        });

        ImageView ivDel = helper.getView(R.id.ivDel);
        final EditText etAddress = helper.getView(R.id.etAddress);

        if (item.getAddress() != null) {
            etAddress.setText(item.getAddress());
        }

        ivDel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (onDelClickListener != null) {
                    onDelClickListener.click(helper.getAdapterPosition());
                    etAddress.setText("");
                }
            }
        });

        etAddress.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                item.setAddress(etAddress.getText().toString());
            }
        });
    }

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public interface OnDelClickListener {
        void click(int pos);

        void select(int pos);
    }
}
