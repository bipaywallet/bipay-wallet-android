package com.spark.bipaywallet.activity.importwallet.fragment;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.agreement.AgreementActivity;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.entity.MyRecordBean;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.MyTextWatcher;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 导入钱包--助记词
 */
public class ImportWalletHelpWordFragment extends ImportWalletBaseFragment {
    public static final String TAG = ImportWalletHelpWordFragment.class.getSimpleName();
    @BindView(R.id.tvImport)
    TextView tvImport;
    //    @BindView(R.id.checkBox)
//    CheckBox checkBox;
    @BindView(R.id.etWords)
    EditText etWords;
    @BindView(R.id.etPassWord)
    EditText etPassWord;
    @BindView(R.id.etConfirmPwd)
    EditText etConfirmPwd;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etTip)
    EditText etTip;

    @BindView(R.id.tvAgree)
    TextView tvAgree;

    private JNIUtil jniUtil;

    public static ImportWalletHelpWordFragment getInstance() {
        ImportWalletHelpWordFragment fragment = new ImportWalletHelpWordFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_import_wallet_help_word;
    }

    @Override
    protected void initData() {
        super.initData();
        jniUtil = JNIUtil.getInstance();
    }

    @OnClick({R.id.tvAgree, R.id.tvImport})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvAgree:
                showActivity(AgreementActivity.class, null);
                break;
            case R.id.tvImport:
                doImport();
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

    private void doImport() {
        String nameStr = etName.getText().toString();
        String wordsStr = etWords.getText().toString();
        String passWordStr = etPassWord.getText().toString();
        String confirmStr = etConfirmPwd.getText().toString();

        String[] mnemonicStrings = new String[]{};
        if (!StringUtils.isEmpty(wordsStr)) {
            mnemonicStrings = wordsStr.split(" ");
        }

        if (StringUtils.isEmpty(nameStr, wordsStr, passWordStr, confirmStr)) {
            ToastUtils.showToast(getString(R.string.Incomplete_information));
        } else if (mnemonicStrings.length != 12) {
            ToastUtils.showToast(getString(R.string.memory_words_warn));
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
                return;
            }

            LogUtils.i(wordsStr.length() + "---");
            String seed = jniUtil.getSeedWithMnemonic(wordsStr);
            if (StringUtils.isEmpty(seed)) {
                ToastUtils.showToast(getString(R.string.memory_words_warn));
            } else {
                jniUtil.freeAlloc(seed);
                LogUtils.i(seed + "---");
                String masterKey = jniUtil.getMasterKey(seed);
                LogUtils.i(masterKey + "---");
                if (!StringUtils.isEmpty(masterKey)) {
                    jniUtil.freeAlloc(masterKey);
                    doVerification(masterKey, passWordStr, nameStr);
                }
            }
        }
    }

    private void doVerification(String masterKey, String password, String name) {
        if (jniUtil != null) {
            //使用钱包下BTC币种的地址判断该钱包是否已经存在
            String qyex = jniUtil.getExPrivKey(masterKey, CoinTypeEnum.BTC.getType());
            jniUtil.freeAlloc(qyex);
            LogUtils.i(qyex + "--qyex");
            String qysubPrivKey = jniUtil.getSubPrivKey(qyex, 2 ^ 31);
            jniUtil.freeAlloc(qysubPrivKey);
            LogUtils.i(qysubPrivKey + "--qysubPrivKey");
            String qyaddress = jniUtil.getCoinAddress(qysubPrivKey, CoinTypeEnum.BTC.getType(), CoinTypeEnum.BTC.getAddressPrefix());
            jniUtil.freeAlloc(qyaddress);
            LogUtils.i(qyaddress + "--qyaddress");
            List<MyCoin> mclist = DataSupport.where("address = ?",
                    qyaddress).find(MyCoin.class);

            //使用钱包下BTC币种的地址判断该钱包是否已经存在
            if (mclist.size() > 0) {
                List<MyCoin> coinList = DataSupport.where("walletName = ?", mclist.get(0).getWalletName()).find(MyCoin.class);
                if (coinList != null && coinList.size() > 0) {
                    for (int k = 0; k < coinList.size(); k++) {
                        DataSupport.deleteAll(MyRecordBean.class, "address = ?", coinList.get(k).getAddress());
                    }
                }
                DataSupport.deleteAll(Wallet.class, "name = ?", mclist.get(0).getWalletName());
                DataSupport.deleteAll(MyCoin.class, "walletName = ?", mclist.get(0).getWalletName());
                DataSupport.deleteAll(MyExchangeRecord.class, "walletName = ?", mclist.get(0).getWalletName());

                doCreate(masterKey, password, name);
            } else {
                doCreate(masterKey, password, name);
            }
        }
    }

    private void doCreate(String masterKey, String password, String name) {
        if (jniUtil != null) {
            LogUtils.i(masterKey + "--masterKey");

            AES mAes = new AES();
            byte[] mBytes = null;

            try {
                mBytes = masterKey.getBytes("UTF8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String enString = mAes.encrypt(mBytes, password);
            LogUtils.i(enString + "--enString");

            if (enString != null) {
                LogUtils.i(DataSupport.findAll(Wallet.class).size() + "---");

                List<String> stringList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(jniUtil.getSupportedCoins());

                    LogUtils.i(jsonArray.length() + "---" + jsonArray.getString(0));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringList.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<MyCoin> list = new ArrayList<>();
                for (int i = 0; i < stringList.size(); i++) {
                    String typeStr = stringList.get(i);
                    CoinTypeEnum coinTypeEnum = CoinTypeEnum.getCoinTypeEnumByName(typeStr.toUpperCase());

                    if (coinTypeEnum != null) {
                        int type = coinTypeEnum.getType();
                        int addressPrefix = coinTypeEnum.getAddressPrefix();

                        if (type == 207) {
                            type = 0;//usdt使用btc的地址
                        }
                        LogUtils.i(type + "--type");

                        String ex = jniUtil.getExPrivKey(masterKey, type);
                        jniUtil.freeAlloc(ex);
                        LogUtils.i(ex + "--ex");

                        String subPrivKey = jniUtil.getSubPrivKey(ex, 2 ^ 31);
                        jniUtil.freeAlloc(subPrivKey);
                        LogUtils.i(subPrivKey + "--subPrivKey");

                        String address = jniUtil.getCoinAddress(subPrivKey, type, addressPrefix);
                        jniUtil.freeAlloc(address);
                        LogUtils.i(address + "--address");

                        byte[] mBytesSub = null;
                        try {
                            mBytesSub = subPrivKey.getBytes("UTF8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String enStringSub = mAes.encrypt(mBytesSub, password);
                        LogUtils.i(enStringSub + "--enStringSub");

                        MyCoin myCoin = new MyCoin();
                        myCoin.setName(coinTypeEnum.getCoinName());
                        myCoin.setSubPrivKey(enStringSub);
                        myCoin.setAddress(address);
                        myCoin.setNum("0.0");
                        myCoin.setCoinType(coinTypeEnum.getType());
                        if (coinTypeEnum.getType() == 0 || coinTypeEnum.getType() == 60) {
                            myCoin.setAdded(true);
                        } else {
                            myCoin.setAdded(false);
                        }
                        myCoin.setWalletName(name);
                        list.add(myCoin);
                    }
                }
                LogUtils.i(list.size() + "---");
                DataSupport.saveAll(list);

                Wallet wallet = new Wallet();
                wallet.setName(name);
                wallet.setEncrypMasterKey(enString);
                wallet.setTip(etTip.getText().toString());
                wallet.save();

                List<Wallet> walletList = DataSupport.findAll(Wallet.class);
                if (walletList != null && walletList.size() > 0) {
                    MyApplication.getApp().setCurrentWallet(walletList.get(walletList.size() - 1));
//                    SharedPreferenceInstance.getInstance().setCurrentWalletPos(walletList.size() - 1);
                    MyApplication.getApp().setChangeWallet(true);
                    SharedPreferenceInstance.getInstance().saveWalletRecordName(MyApplication.getApp().getCurrentWallet().getName());
                }

                ((ImportWalletCallback) getActivity()).success();
            }
        }
    }

}
