package com.spark.bipaywallet.activity.walletdetails;


import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.exportprivatekey.ExportPrivatekeyActivity;
import com.spark.bipaywallet.activity.updatepassword.UpdatePasswordActivity;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.dialog.PasswordDialog;
import com.spark.bipaywallet.dialog.WhiteCenterDialog;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包详情
 */

public class WalletDetailsActivity extends BaseActivity {
    public static final int ISDELETE_WALLET = 1;
    @BindView(R.id.llDefault)
    LinearLayout llDefault;

    @BindView(R.id.llUpdatePassword)
    LinearLayout llUpdatePassword;
    @BindView(R.id.llExport)
    LinearLayout llExport;
    @BindView(R.id.llWalletName)
    LinearLayout llWalletName;
    @BindView(R.id.llPasswordTip)
    LinearLayout llPasswordTip;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.tvTip)
    TextView tvTip;

    private AES mAes;

    private String walletName;
    private Wallet wallet;

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCurData();
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_wallet_details;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }


    @Override
    protected void initData() {
        super.initData();
        mAes = new AES();
    }

    @OnClick({R.id.llUpdatePassword, R.id.llWalletName, R.id.llExport, R.id.tvDel})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llWalletName:
                showUpdateNameDialog();
                break;
            case R.id.llUpdatePassword:
                if (wallet != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("wallet", wallet);
                    showActivity(UpdatePasswordActivity.class, bundle);
                }
                break;
            case R.id.llExport:
                Bundle bundle = new Bundle();
                bundle.putSerializable("walletName", walletName);
                showActivity(ExportPrivatekeyActivity.class, bundle);
                break;
            case R.id.tvDel:
                showPasswordDialog(ISDELETE_WALLET);
                break;
        }
    }

    private void showUpdateNameDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_update_wallet_name, null);
        final WhiteCenterDialog whiteCenterDialog = new WhiteCenterDialog(this);
        whiteCenterDialog.setContentView(contentView);
        whiteCenterDialog.show();

        final EditText etName = contentView.findViewById(R.id.etName);

        TextView tvConfirm = contentView.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = etName.getText().toString();
                if (StringUtils.isEmpty(nameStr)) {
                    ToastUtils.showToast(getString(R.string.Incomplete_information));
                } else {
                    List<Wallet> list = DataSupport.where("name = ?",
                            nameStr).find(Wallet.class);

                    if (list.size() > 0) {
                        ToastUtils.showToast(getString(R.string.wallet_name_repeat_warn));
                    } else {
                        whiteCenterDialog.dismiss();

                        ContentValues values = new ContentValues();
                        values.put("name", nameStr);
                        DataSupport.updateAll(Wallet.class, values, "name = ?", wallet.getName());

                        ContentValues myCoinValues = new ContentValues();
                        myCoinValues.put("walletName", nameStr);
                        DataSupport.updateAll(MyCoin.class, myCoinValues, "walletName = ?", wallet.getName());

                        List<Wallet> wll = DataSupport.where("name = ?", nameStr).find(Wallet.class);
                        if (MyApplication.getApp().getCurrentWallet().getName().equals(wallet.getName())) {
                            if (wll != null && wll.size() > 0) {
                                MyApplication.getApp().setCurrentWallet(wll.get(0));
                                SharedPreferenceInstance.getInstance().saveWalletRecordName(MyApplication.getApp().getCurrentWallet().getName());
                            }
                        }

                        if (wll != null && wll.size() > 0) {
                            wallet = wll.get(0);
                        }
                        walletName = nameStr;
                        tvName.setText(nameStr);
                        setTitle(wallet.getName());

                        ToastUtils.showToast(getString(R.string.password_change_success));

                        MyApplication.getApp().setChangeWallet(true);
                    }
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

    private void showPasswordDialog(final int key) {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                if (mAes != null && wallet != null) {
                    String deString = mAes.decrypt(wallet.getEncrypMasterKey(), password);
                    if (deString != null) {
                        passwordDialog.dismiss();

                        switch (key) {
//                            case ISEXPORT_KEY:
//                                showOutputKeyDialog(deString);
//                                break;
                            case ISDELETE_WALLET:
                                showDelDialog();
                                break;
                        }
                    } else {
                        ToastUtils.showToast(getString(R.string.password_confirm_error));
                    }
                }
            }

            @Override
            public void doCancel() {
                passwordDialog.dismiss();
            }
        });
    }

//    private void showOutputKeyDialog(final String key) {
//        final ExportKeyDialog exportKeyDialog = new ExportKeyDialog(this, key);
//        exportKeyDialog.show();
//        exportKeyDialog.setClicklistener(new ExportKeyDialog.ClickListenerInterface() {
//            @Override
//            public void doCopy() {
//                WonderfulCommonUtils.copyText(WalletDetailsActivity.this, key);
//            }
//
//            @Override
//            public void doCancel() {
//                exportKeyDialog.dismiss();
//            }
//        });
//    }

    private void showDelDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_wallet, null);
        final WhiteCenterDialog whiteCenterDialog = new WhiteCenterDialog(this);
        whiteCenterDialog.setContentView(contentView);
        whiteCenterDialog.show();

        TextView tvConfirm = contentView.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteCenterDialog.dismiss();
                if (wallet != null) {
                    List<MyCoin> coinList = DataSupport.where("walletName = ?", wallet.getName()).find(MyCoin.class);
                    if (coinList != null && coinList.size() > 0) {
                        for (int k = 0; k < coinList.size(); k++) {
                            DataSupport.deleteAll(MyRecordBean.class, "address = ?", coinList.get(k).getAddress());
                        }
                    }
                    DataSupport.deleteAll(Wallet.class, "name = ?", wallet.getName());
                    DataSupport.deleteAll(MyCoin.class, "walletName = ?", wallet.getName());
                    DataSupport.deleteAll(MyExchangeRecord.class, "walletName = ?", wallet.getName());

                    if (MyApplication.getApp().getCurrentWallet().getName().equals(wallet.getName())) {
                        List<Wallet> walletList = DataSupport.findAll(Wallet.class);
                        if (walletList != null && walletList.size() > 0) {
                            MyApplication.getApp().setCurrentWallet(walletList.get(0));
//                            SharedPreferenceInstance.getInstance().setCurrentWalletPos(0);
                            SharedPreferenceInstance.getInstance().saveWalletRecordName(MyApplication.getApp().getCurrentWallet().getName());
                        } else {
                            MyApplication.getApp().setCurrentWallet(null);
//                            SharedPreferenceInstance.getInstance().setCurrentWalletPos(-1);
                        }

                        MyApplication.getApp().setChangeWallet(true);
                    }

                    if (SharedPreferenceInstance.getInstance().getWalletRecordName() != null &&
                            SharedPreferenceInstance.getInstance().getWalletRecordName().equals(wallet.getName())) {

                        SharedPreferenceInstance.getInstance().saveWalletRecordName(null);
                    }

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

    @Override
    protected void obtainData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            walletName = bundle.getString("walletName");
        }
    }

    @Override
    protected void loadData() {
        loadCurData();
    }

    private void loadCurData() {
        if (walletName != null) {
            List<Wallet> walletList = DataSupport.where("name = ?", walletName).find(Wallet.class);
            if (walletList != null && walletList.size() > 0) {
                wallet = walletList.get(0);
                if (wallet != null) {
//                    List<MyCoin> coinList = DataSupport.where("walletName = ? and isAdded = ?",
//                            wallet.getName(), "1").find(MyCoin.class);
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int i = 0; i < coinList.size(); i++) {
//                        stringBuffer.append(coinList.get(i).getName() + " ");
//                    }
//                    tvSelectedCoin.setText(stringBuffer.toString());

                    tvName.setText(wallet.getName());
                    setTitle(wallet.getName());
                    if (!StringUtils.isEmpty(wallet.getTip())) {
                        tvTip.setText(wallet.getTip());
                    } else {
                        llPasswordTip.setVisibility(View.GONE);
                        line1.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


}
