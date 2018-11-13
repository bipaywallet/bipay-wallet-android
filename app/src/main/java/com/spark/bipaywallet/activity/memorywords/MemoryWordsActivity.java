package com.spark.bipaywallet.activity.memorywords;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.createwallet.CreateSuccessActivity;
import com.spark.bipaywallet.adapter.ConfirmWordsAdapter;
import com.spark.bipaywallet.adapter.SelectedWordsAdapter;
import com.spark.bipaywallet.aes.AES;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.utils.DpPxUtils;
import com.spark.bipaywallet.utils.LogUtils;
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
 * 助记词
 */

public class MemoryWordsActivity extends BaseActivity {
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.llDefault)
    LinearLayout llDefault;
    @BindView(R.id.llQrzjc)
    LinearLayout llQrzjc;
    @BindView(R.id.rvWords)
    RecyclerView rvWords;
    @BindView(R.id.rvConfirmWords)
    RecyclerView rvConfirmWords;
    @BindView(R.id.rvSelectWords)
    RecyclerView rvSelectWords;
    private ArrayList<String> wordsList;
    private ArrayList<String> confirmList;
    private ArrayList<String> selectList;
    private ArrayList<Integer> confirmIndexList;
    private ArrayList<Integer> selectIndexList;
    private ConfirmWordsAdapter wordsAdapter;
    private ConfirmWordsAdapter confirmAdapter;
    private SelectedWordsAdapter selectAdapter;

    private boolean isChina = true;
    //    private String[] selectStrings;
    private int currentIndex = -1;

    private String mnemonicStrChina = "";
    private String mnemonicStrEng = "";
    private String[] mnemonicStrings = new String[]{};
    private String name;
    private String password;
    private String tip;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_memorywords;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText(getString(R.string.english));
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_one_pop_cjqb));
        jniUtil = JNIUtil.getInstance();
        wordsList = new ArrayList<>();
        confirmList = new ArrayList<>();
        confirmIndexList = new ArrayList<>();
        selectList = new ArrayList<>();
        selectIndexList = new ArrayList<>();
        if (jniUtil != null) {
            mnemonicStrChina = jniUtil.getMnemonic(8);
            jniUtil.freeAlloc(mnemonicStrChina);
            mnemonicStrEng = jniUtil.getMnemonic(0);
            jniUtil.freeAlloc(mnemonicStrEng);
        }
        initRv();
        setGridView();
    }

    @Override
    protected void obtainData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            password = bundle.getString("password");
            LogUtils.i("password==" + password);
            tip = bundle.getString("tip");
        }
    }

    @OnClick({R.id.tvGoto, R.id.tvNext, R.id.tvConfirm})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvGoto:
                switchLanguage();
                break;
            case R.id.tvNext:
                doNext();
                llDefault.setVisibility(View.GONE);
                llQrzjc.setVisibility(View.VISIBLE);
                tvGoto.setVisibility(View.INVISIBLE);
                break;
            case R.id.tvConfirm:
                boolean isShow = true;
                for (int i = 0; i < 12; i++) {
                    if (confirmList.size() < i || StringUtils.isEmpty(confirmList.get(i))) {
                        isShow = false;
                    } else if (!confirmList.get(i).equals(mnemonicStrings[i])) {
                        isShow = false;
                    }
                }
                if (isShow) {
                    showMyDialog(1);
                } else {
                    ToastUtils.showToast(getString(R.string.confirm_error));
                }
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog(0);
            }
        });
    }

    /**
     * 切换语言
     */
    private void switchLanguage() {
        if (isChina) {
            tvGoto.setText(R.string.chinese);
            isChina = !isChina;
            setGridView();
            wordsAdapter.notifyDataSetChanged();
        } else {
            tvGoto.setText(R.string.english);
            isChina = !isChina;
            setGridView();
            wordsAdapter.notifyDataSetChanged();
        }
    }

    private void initRv() {
        GridLayoutManager wordsGridManager = new GridLayoutManager(this, 4);
        rvWords.setLayoutManager(wordsGridManager);
        wordsAdapter = new ConfirmWordsAdapter(this, wordsList);
        rvWords.setAdapter(wordsAdapter);

        GridLayoutManager confirmGridManager = new GridLayoutManager(this, 4);
        rvConfirmWords.setLayoutManager(confirmGridManager);

        GridLayoutManager selectGridManager = new GridLayoutManager(this, 4);
        rvSelectWords.setLayoutManager(selectGridManager);
        rvSelectWords.addItemDecoration(new GridSpacingItemDecoration(4, DpPxUtils.dip2px(this, 10)));
    }

    private void doCreate() {
        if (jniUtil != null) {
            String mnemonic;
            if (isChina) {
                mnemonic = mnemonicStrChina;
            } else {
                mnemonic = mnemonicStrEng;
            }

            if (!StringUtils.isEmpty(mnemonic)) {
                String seed = jniUtil.getSeedWithMnemonic(mnemonic);
                if (!StringUtils.isEmpty(seed)) {
                    jniUtil.freeAlloc(seed);
                    String masterKey = jniUtil.getMasterKey(seed);
                    if (!StringUtils.isEmpty(masterKey)) {
                        jniUtil.freeAlloc(masterKey);
                        Log.i("sx", masterKey + "--masterKey");

                        AES mAes = new AES();
                        byte[] mBytes = null;

                        try {
                            mBytes = masterKey.getBytes("UTF8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String enString = mAes.encrypt(mBytes, password);
                        Log.i("sx", enString + "--enString");

                        if (enString != null) {
                            Log.i("sx", DataSupport.findAll(Wallet.class).size() + "---");

                            List<String> stringList = new ArrayList<>();
                            try {
                                JSONArray jsonArray = new JSONArray(jniUtil.getSupportedCoins());

                                Log.i("sx", jsonArray.length() + "---" + jsonArray.getString(0));

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
                                    Log.i("sx", type + "--type");

                                    String ex = jniUtil.getExPrivKey(masterKey, type);
                                    jniUtil.freeAlloc(ex);
                                    Log.i("sx", ex + "--ex");

                                    String subPrivKey = jniUtil.getSubPrivKey(ex, 2 ^ 31);
                                    jniUtil.freeAlloc(subPrivKey);
                                    Log.i("sx", subPrivKey + "--subPrivKey");

                                    String address = jniUtil.getCoinAddress(subPrivKey, type, addressPrefix);
                                    jniUtil.freeAlloc(address);
                                    Log.i("sx", address + "--address");

                                    byte[] mBytesSub = null;
                                    try {
                                        mBytesSub = subPrivKey.getBytes("UTF8");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String enStringSub = mAes.encrypt(mBytesSub, password);
                                    Log.i("sx", enStringSub + "--enStringSub");

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
                            Log.i("sx", list.size() + "---");
                            DataSupport.saveAll(list);

                            Wallet wallet = new Wallet();
                            wallet.setName(name);
                            wallet.setEncrypMasterKey(enString);
                            if (tip != null) {
                                wallet.setTip(tip);
                            }
                            wallet.save();

                            List<Wallet> walletList = DataSupport.findAll(Wallet.class);
                            if (walletList != null && walletList.size() > 0) {
                                MyApplication.getApp().setCurrentWallet(walletList.get(walletList.size() - 1));
//                                SharedPreferenceInstance.getInstance().setCurrentWalletPos(walletList.size() - 1);
                                MyApplication.getApp().setChangeWallet(true);
                                SharedPreferenceInstance.getInstance().saveWalletRecordName(MyApplication.getApp().getCurrentWallet().getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private void setGridView() {
        if (isChina && mnemonicStrChina.length() > 0) {
            mnemonicStrings = mnemonicStrChina.split(" ");
        } else if (mnemonicStrEng.length() > 0) {
            mnemonicStrings = mnemonicStrEng.split(" ");
        }

        if (mnemonicStrings.length > 0) {
            wordsList.clear();
            for (int i = 0; i < 12; i++) {
                wordsList.add(mnemonicStrings[i]);
            }
        }
    }

    private void doNext() {
        for (int i = 0; i < 12; i++) {
            confirmList.add("");
        }
        confirmAdapter = new ConfirmWordsAdapter(this, confirmList);
        rvConfirmWords.setAdapter(confirmAdapter);
        confirmAdapter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (confirmList != null && currentIndex >= 0) {
                    if (confirmIndexList.size() > position) {
//                        selectAdapter.notifyItemChanged(confirmIndexList.get(position));
                        selectIndexList.add(confirmIndexList.get(position));
                        selectList.set(11 - currentIndex, mnemonicStrings[confirmIndexList.get(position)]);
                        selectAdapter.notifyDataSetChanged();

                        confirmList.remove(position);
                        confirmList.add("");
                        confirmIndexList.remove(position);
                        confirmAdapter.notifyDataSetChanged();
                        currentIndex--;
                    }
                }
            }
        });

        List<Integer> integerList = getRandomList(12);
        if (integerList.size() >= 12 && mnemonicStrings.length > 0) {
            selectIndexList.addAll(integerList);
//            selectStrings = new String[12];
            for (int i = 0; i < 12; i++) {
//                selectStrings[i] = mnemonicStrings[integerList.get(i)];
                selectList.add(mnemonicStrings[integerList.get(i)]);
            }

//            if (selectStrings.length > 0) {
//                for (int i = 0; i < 12; i++) {
//                    selectList.add(selectStrings[i]);
//                }
//            }
        }

        selectAdapter = new SelectedWordsAdapter(this, selectList);
        rvSelectWords.setAdapter(selectAdapter);
        selectAdapter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentIndex >= -1 && currentIndex < 11 && mnemonicStrings != null && mnemonicStrings.length > position) {
//                    confirmList.set(++currentIndex, selectStrings[position]);

                    if (selectIndexList != null && selectIndexList.size() > position) {
                        confirmList.set(++currentIndex, mnemonicStrings[selectIndexList.get(position)]);

                        confirmIndexList.add(selectIndexList.get(position));
                        confirmAdapter.notifyDataSetChanged();

                        selectIndexList.remove(position);
                        selectList.remove(position);
                        selectList.add("");
                        selectAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //获取0-size之间随机数不重复
    private static List<Integer> getRandomList(int size) {
        List<Integer> key = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            key.add(i);
        }
        for (int i = 0; i < size; i++) {
            boolean flag = true;
            while (flag) {
                int randmonnum = (int) Math.round(Math.random() * (size - 1));
                int index = key.indexOf(randmonnum);
                if (index >= 0) {
                    flag = false;
                    key.set(index, size);
                    result.add(randmonnum);
                    break;
                }
            }
        }
        return result;
    }

    //设置间距
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;//列数
        private int spacing;//间距

        public GridSpacingItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        }
    }

    @Override
    public void onBackPressed() {
        showMyDialog(0);
    }

    /**
     * 返回提示、确认提示
     *
     * @param type 0-点击返回按钮，1-确认钱包助记词
     */
    private void showMyDialog(final int type) {
        String title = "";
        if (type == 0) {
            title = getString(R.string.dialog_back_create_wallet_tip);
        } else {
            title = getString(R.string.dialog_memory_title);
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.warm_prompt)).btnText(getString(R.string.dialog_cancel), getString(R.string.dialog_confirm))
                .titleTextColor(getResources().getColor(R.color.black))
                .btnTextColor(getResources().getColor(R.color.dialog_cancel_txt), getResources().getColor(R.color.dialog_ok_txt))
                .content(title).contentTextColor(getResources().getColor(R.color.font_sec_black)).setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if (type == 1) {
                            doCreate();
                            showActivity(CreateSuccessActivity.class, null);
                        }
                        finish();
                        dialog.superDismiss();

                    }
                });
        dialog.show();
    }

}
