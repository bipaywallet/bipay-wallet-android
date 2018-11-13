package com.spark.bipaywallet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordDialog extends Dialog {
    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private GridView gridView;
    private ArrayList<Map<String, String>> valueList;
    private TextView[] tvList;
    private int currentIndex = -1;

    private String strPassword;

    public interface ClickListenerInterface {

        void doConfirm(String password);

        void doCancel();
    }

    public PasswordDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.dialog_password);

        TextView tvConfirm = findViewById(R.id.tvConfirm);
        TextView tvCancel = findViewById(R.id.tvCancel);

        gridView = findViewById(R.id.gvKeyboard);
        valueList = new ArrayList<>();
        tvList = new TextView[6];

        tvList[0] = findViewById(R.id.tvPass1);
        tvList[1] = findViewById(R.id.tvPass2);
        tvList[2] = findViewById(R.id.tvPass3);
        tvList[3] = findViewById(R.id.tvPass4);
        tvList[4] = findViewById(R.id.tvPass5);
        tvList[5] = findViewById(R.id.tvPass6);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = CommonUtils.getScreenWidth();
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);

        setGridView();
    }

    private void setGridView() {
        /* 初始化按钮上应该显示的数字 */
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<String, String>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 12) {
                map.put("name", "<<-");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            }
            valueList.add(map);
        }

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 11 && position != 9) {    //点击0~9按钮
                    if (currentIndex >= -1 && currentIndex < 5) {      //判断输入位置————要小心数组越界
                        tvList[++currentIndex].setText(valueList.get(position).get("name"));
                    }
                } else {
                    if (position == 11) {      //点击退格键
                        if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                            tvList[currentIndex--].setText("");
                        }
                    }
                }
            }
        });
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
                    strPassword = "";
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }

                    if (strPassword.length() == 6) {
                        clickListenerInterface.doConfirm(strPassword);
                    } else {
                        ToastUtils.showToast(context.getString(R.string.password_length_tip));
                    }
                    break;
                case R.id.tvCancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

    //GrideView的适配器
    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_grid, null);
                viewHolder = new ViewHolder();
                viewHolder.btnKey = convertView.findViewById(R.id.btn_keys);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.btnKey.setText(valueList.get(position).get("name"));
            if (position == 9) {
                viewHolder.btnKey.setBackgroundResource(R.color.lineGray);
                viewHolder.btnKey.setEnabled(false);
            }
            if (position == 11) {
                viewHolder.btnKey.setBackgroundResource(R.drawable.keyboard_cancel_background_selector);
            }

            return convertView;
        }
    };

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView btnKey;
    }


}
