package com.spark.bipaywallet.activity.createwallet;


import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.agreement.AgreementActivity;
import com.spark.bipaywallet.activity.memorywords.MemoryWordsActivity;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建钱包
 */

public class CreateWalletActivity extends BaseActivity {
//    @BindView(R.id.checkBox)
//    CheckBox checkBox;
    @BindView(R.id.tvCreate)
    TextView tvCreate;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPassWord)
    EditText etPassWord;
    @BindView(R.id.etConfirmPwd)
    EditText etConfirmPwd;
    @BindView(R.id.etTip)
    EditText etTip;
    @BindView(R.id.tvAgree)
    TextView tvAgree;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_one_pop_cjqb));
    }


    @OnClick({R.id.tvAgree, R.id.tvCreate})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvAgree:
                showActivity(AgreementActivity.class, null);
                break;
            case R.id.tvCreate:
                doCreate();
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        etPassWord.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etPassWord.getText().toString().length() > 6) {
                    ToastUtils.showToast(getString(R.string.password_length_tip));
                    etPassWord.getText().delete(6, 7);
                }
            }
        });

        etConfirmPwd.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etConfirmPwd.getText().toString().length() > 6) {
                    ToastUtils.showToast(getString(R.string.password_length_tip));
                    etConfirmPwd.getText().delete(6, 7);
                }
            }
        });
    }

    /**
     * 创建钱包
     */
    private void doCreate() {
        String nameStr = etName.getText().toString().trim();
        String passWordStr = etPassWord.getText().toString().trim();
        String confirmStr = etConfirmPwd.getText().toString().trim();
        String tipStr = etTip.getText().toString().trim();
        if (StringUtils.isEmpty(nameStr, passWordStr, confirmStr)) {
            ToastUtils.showToast(getString(R.string.Incomplete_information));
        } else if (etPassWord.length() != 6 || etConfirmPwd.length() != 6) {
            ToastUtils.showToast(getString(R.string.password_length_tip));
        } else if (!passWordStr.equals(confirmStr)) {
            ToastUtils.showToast(getString(R.string.password_two_warn));
        }
//        else if (!checkBox.isChecked()) {
//            ToastUtils.showToast(getString(R.string.check_box_selected_first));
//        }
        else {
            List<Wallet> list = DataSupport.where("name = ?",
                    nameStr).find(Wallet.class);
            if (list.size() > 0) {
                ToastUtils.showToast(getString(R.string.wallet_name_repeat_warn));
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("name", nameStr);
                bundle.putString("password", passWordStr);
                bundle.putString("tip", tipStr);
                showActivity(MemoryWordsActivity.class, bundle);
                finish();
            }
        }
    }

}
