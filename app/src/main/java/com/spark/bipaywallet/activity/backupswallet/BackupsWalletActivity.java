package com.spark.bipaywallet.activity.backupswallet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.dialog.PasswordDialog;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import butterknife.BindView;

/**
 * 备份钱包
 */

public class BackupsWalletActivity extends BaseActivity {
    @BindView(R.id.tvBackups)
    TextView tvBackups;

    private AES mAes;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BackupsWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_backups_wallet;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAes = new AES();

        tvBackups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordDialog();
            }
        });
    }

    private void showPasswordDialog() {
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setClicklistener(new PasswordDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String password) {
                passwordDialog.dismiss();
                if (mAes != null) {
                    Log.i("sx", DataSupport.findAll(Wallet.class).size() + "---size");
                    Wallet w = DataSupport.findFirst(Wallet.class);
                    if (w != null) {
                        String deString = mAes.decrypt(w.getEncrypMasterKey(), password);
                        if (deString != null) {
                            Log.i("sx", deString + "--deString");
                            finish();
//                            MemoryWordsActivity.actionStart(BackupsWalletActivity.this);
                        } else {
                            ToastUtils.showToast(getString(R.string.password_confirm_error));
                        }
                    } else {
                        Log.i("sx", "null");
                    }
                }
            }

            @Override
            public void doCancel() {
                passwordDialog.dismiss();
            }
        });
    }

}
