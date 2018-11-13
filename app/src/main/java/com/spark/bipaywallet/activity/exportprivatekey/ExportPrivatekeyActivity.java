package com.spark.bipaywallet.activity.exportprivatekey;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.NameAdapter;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.dialog.ExportKeyDialog;
import com.spark.bipaywallet.dialog.PasswordDialog;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 导出私钥
 */

public class ExportPrivatekeyActivity extends BaseActivity {
    @BindView(R.id.llWalletName)
    LinearLayout llWalletName;
    @BindView(R.id.tvWalletName)
    TextView tvWalletName;
    @BindView(R.id.rvCoin)
    RecyclerView rvCoin;

    private NameAdapter adapter;
    private String walletName;
    private Wallet wallet;
    private List<MyCoin> myCoinList = new ArrayList<>();

    private JNIUtil jniUtil;
    private AES mAes;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_export_key;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_wallet_details_dcsy));
        jniUtil = JNIUtil.getInstance();
        mAes = new AES();
        initRv();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            walletName = bundle.getString("walletName");
        }
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvCoin.setLayoutManager(manager);
        adapter = new NameAdapter(this, R.layout.item_coin_key, myCoinList);
        rvCoin.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (myCoinList.size() > position)
                    showCoinPasswordDialog(myCoinList.get(position));
            }
        });
    }

    @Override
    protected void loadData() {
        if (walletName != null) {
//            tvWalletName.setText(walletName + " " + getString(R.string.activity_wallet_details_main_key));
            List<Wallet> walletList = DataSupport.where("name = ?", walletName).find(Wallet.class);

            if (walletList != null && walletList.size() > 0) {
                wallet = walletList.get(0);

                if (wallet != null) {
                    List<MyCoin> coinList = DataSupport.where("walletName = ? and isAdded = ?",
                            wallet.getName(), "1").find(MyCoin.class);

                    if (coinList != null && coinList.size() > 0) {
                        myCoinList.clear();
                        myCoinList.addAll(coinList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @OnClick(R.id.llWalletName)
    void showPasswordDialog() {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                if (mAes != null && wallet != null) {
                    String deString = mAes.decrypt(wallet.getEncrypMasterKey(), password);
                    if (deString != null) {
                        passwordDialog.dismiss();
                        LogUtils.i(deString + "--deString");

                        showOutputKeyDialog(deString, walletName + " " + getString(R.string.activity_wallet_details_main_key));
                    } else {
                        ToastUtils.showToast(getString(R.string.password_confirm_error));
                    }
                } else {
                    LogUtils.i("null");
                }
            }

            @Override
            public void doCancel() {
                passwordDialog.dismiss();
            }
        });
    }

    private void showCoinPasswordDialog(final MyCoin myCoin) {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                if (mAes != null && myCoin != null && myCoin.getSubPrivKey() != null) {
                    String deSubKey = mAes.decrypt(myCoin.getSubPrivKey(), password);

                    if (deSubKey != null) {
                        CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(myCoin.getName());
                        int keyPrefix = -1;
                        if (coinTypeEnum != null) {
                            keyPrefix = coinTypeEnum.getKeyPrefix();
                        }
                        String signPrivk = jniUtil.getSignaturePrivKey(deSubKey, myCoin.getCoinType(), keyPrefix);

                        if (signPrivk != null) {
                            passwordDialog.dismiss();
                            LogUtils.i(signPrivk + "--signPrivk");

                            showOutputKeyDialog(signPrivk, myCoin.getName() + " " + getString(R.string.activity_wallet_details_key));
                        }
                    } else {
                        ToastUtils.showToast(getString(R.string.password_confirm_error));
                    }
                } else {
                    LogUtils.i("null");
                }
            }

            @Override
            public void doCancel() {
                passwordDialog.dismiss();
            }
        });
    }

    private void showOutputKeyDialog(final String key, String title) {
        final ExportKeyDialog exportKeyDialog = new ExportKeyDialog(this, key, title);
        exportKeyDialog.show();
        exportKeyDialog.setClicklistener(new ExportKeyDialog.ClickListenerInterface() {
            @Override
            public void doCopy() {
                CommonUtils.copyText(activity, key);
            }

            @Override
            public void doCancel() {
                exportKeyDialog.dismiss();
            }
        });
    }


}
