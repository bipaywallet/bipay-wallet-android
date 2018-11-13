package com.spark.bipaywallet.activity.updatepassword;


import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.dialog.WhiteCenterDialog;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 */

public class UpdatePasswordActivity extends BaseActivity {
    @BindView(R.id.tvChange)
    TextView tvChange;
    @BindView(R.id.etOld)
    EditText etOld;
    @BindView(R.id.etNew)
    EditText etNew;
    @BindView(R.id.etConfirm)
    EditText etConfirm;
    private AES mAes;
    private Wallet wallet;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_update_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }


    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_update_password_title));
        mAes = new AES();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            wallet = (Wallet) bundle.getSerializable("wallet");
        }
    }


    @Override
    protected void setListener() {
        super.setListener();
        etOld.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etOld.getText().toString().length() > 6) {
                    ToastUtils.showToast(getString(R.string.password_length_tip));
                    etOld.getText().delete(6, 7);
                }
            }
        });

        etNew.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etNew.getText().toString().length() > 6) {
                    ToastUtils.showToast(getString(R.string.password_length_tip));
                    etNew.getText().delete(6, 7);
                }
            }
        });

        etConfirm.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etConfirm.getText().toString().length() > 6) {
                    ToastUtils.showToast(getString(R.string.password_length_tip));
                    etConfirm.getText().delete(6, 7);
                }
            }
        });

    }

    @OnClick(R.id.tvChange)
    void doChange() {
        String oldStr = etOld.getText().toString();
        String newStr = etNew.getText().toString();
        String confirmStr = etConfirm.getText().toString();

        if (StringUtils.isEmpty(oldStr, newStr, confirmStr)) {
            ToastUtils.showToast(getString(R.string.Incomplete_information));
        } else if (oldStr.length() != 6 || newStr.length() != 6 || confirmStr.length() != 6) {
            ToastUtils.showToast(getString(R.string.password_length_tip));
        } else if (!newStr.equals(confirmStr)) {
            ToastUtils.showToast(getString(R.string.password_two_warn));
        } else {
            if (mAes != null) {
                if (wallet != null) {
                    String deString = mAes.decrypt(wallet.getEncrypMasterKey(), oldStr);
                    if (deString != null) {
                        showDelDialog(deString, oldStr, newStr);
                    } else {
                        ToastUtils.showToast(getString(R.string.password_confirm_error));
                    }
                }
            }
        }
    }

    private void showDelDialog(final String deString, final String oldStr, final String newStr) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        final WhiteCenterDialog whiteCenterDialog = new WhiteCenterDialog(this);
        whiteCenterDialog.setContentView(contentView);
        whiteCenterDialog.show();

        TextView tvConfirm = contentView.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteCenterDialog.dismiss();

                byte[] mBytes = null;

                try {
                    mBytes = deString.getBytes("UTF8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String enString = mAes.encrypt(mBytes, newStr);

                if (enString != null) {
                    ContentValues values = new ContentValues();
                    values.put("encrypMasterKey", enString);
                    //修改钱包加密主私钥
                    DataSupport.updateAll(Wallet.class, values, "name = ?", wallet.getName());

                    //修改钱包对应所有币种加密私钥
                    List<MyCoin> coinList = DataSupport.where("walletName = ?",
                            wallet.getName()).find(MyCoin.class);

                    if (coinList != null && coinList.size() > 0) {
                        for (int i = 0; i < coinList.size(); i++) {
                            String deSubKey = mAes.decrypt(coinList.get(i).getSubPrivKey(), oldStr);

                            if (deSubKey != null) {
                                byte[] mBytesSub = null;
                                try {
                                    mBytesSub = deSubKey.getBytes("UTF8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String enStringSub = mAes.encrypt(mBytesSub, newStr);

                                if (enStringSub != null) {
                                    ContentValues coinValues = new ContentValues();
                                    coinValues.put("subPrivKey", enStringSub);
                                    DataSupport.updateAll(MyCoin.class, coinValues, "walletName = ? and name = ?", wallet.getName(), coinList.get(i).getName());
                                }
                            }
                        }
                    }

                    if (MyApplication.getApp().getCurrentWallet().getName().equals(wallet.getName())) {
                        List<Wallet> list = DataSupport.where("name = ?", wallet.getName()).find(Wallet.class);

                        if (list != null && list.size() > 0) {
                            MyApplication.getApp().setCurrentWallet(list.get(0));
                            SharedPreferenceInstance.getInstance().saveWalletRecordName(MyApplication.getApp().getCurrentWallet().getName());
                        }
                    }

                    ToastUtils.showToast(getString(R.string.password_change_success));
                    finish();
                }
            }
        });

        TextView tvCancel = contentView.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteCenterDialog.dismiss();
            }
        });
    }

}
