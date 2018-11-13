package com.spark.bipaywallet.activity.editman;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.EditTextAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.AddressBean;
import com.spark.bipaywallet.entity.LinkMan;
import com.spark.bipaywallet.utils.HanziToPinyin;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 编辑联系人
 */

public class EditManActivity extends BaseActivity {
    @BindView(R.id.rvAddress)
    RecyclerView rvAddress;
    @BindArray(R.array.currency_str)
    String[] currencyStrs;
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etRemarks)
    EditText etRemarks;
    @BindView(R.id.tvCoinType)
    TextView tvCoinType;
    private EditTextAdapter adapter;
    private LinkMan linkMan;
    private ArrayList<AddressBean> addressList;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etAddress1)
    EditText etAddress1;
    @BindView(R.id.tvSave)
    TextView tvSave;
    private List<String> coinNameList = new ArrayList<>();
    private String currCoinName = "BTC";
    private NormalListDialog normalDialog = null;
    private boolean isItem;
    private int itemPos;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_create_man;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText(getString(R.string.activity_edit_man_del));
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_edit_man_title));
        addressList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvAddress.setLayoutManager(manager);
        coinNameList = Arrays.asList(this.currencyStrs);
        adapter = new EditTextAdapter(this, R.layout.adapter_address_edit_text, addressList);
        rvAddress.setAdapter(adapter);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            linkMan = (LinkMan) bundle.getSerializable("linkMan");
            if (linkMan != null) {
                etName.setText(linkMan.getName());
                etPhoneNumber.setText(linkMan.getPhoneNumber());
                etEmail.setText(linkMan.getEmail());
                etRemarks.setText(linkMan.getRemarks());
                List<AddressBean> abList = DataSupport.where("linkManName = ?", linkMan.getName()).find(AddressBean.class);
                if (abList != null && abList.size() > 0) {
                    int index = 0;
                    if (abList.get(0).getCoinName() != null) {
                        for (int i = 0; i < coinNameList.size(); i++) {
                            if (abList.get(0).getCoinName().equals(coinNameList.get(i))) {
                                index = i;
                                break;
                            }
                        }
                    }
                    tvCoinType.setText(coinNameList.get(index));
                    etAddress1.setText(abList.get(0).getAddress());
                    for (int i = 1; i < abList.size(); i++) {
                        addressList.add(abList.get(i));
                        adapter.notifyItemInserted(addressList.size() - 1);
                        LogUtils.i(addressList.size() + "szszs");
                    }
                }
            }
        }

    }

    @OnClick({R.id.tvGoto, R.id.ivAddress, R.id.tvSave, R.id.tvCoinType})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvGoto:
                showDelDialog();
                break;
            case R.id.ivAddress:
                AddressBean addressBean = new AddressBean();
                addressBean.setCoinName(currencyStrs[0]);
                addressList.add(addressBean);
                adapter.notifyItemInserted(addressList.size() - 1);
                break;
            case R.id.tvSave:
                doSave();
                break;
            case R.id.tvCoinType:
                isItem = false;
                showListDialog();
                break;
        }
    }

    /**
     * 列表选择框
     */
    private void showListDialog() {
        if (normalDialog == null) {
            normalDialog = new NormalListDialog(activity, currencyStrs);
            normalDialog.title(getString(R.string.coin_type));
            normalDialog.titleBgColor(getResources().getColor(R.color.main_head_bg));
            normalDialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isItem) {
                        String itemCoinName = currencyStrs[position];
                        AddressBean addressBean = addressList.get(itemPos);
                        addressBean.setCoinName(itemCoinName);
                        adapter.notifyItemChanged(itemPos);
                    } else {
                        currCoinName = currencyStrs[position];
                        tvCoinType.setText(currCoinName);
                    }
                    normalDialog.dismiss();
                }
            });
        }
        normalDialog.show();
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setOnDelClickListener(new EditTextAdapter.OnDelClickListener() {
            @Override
            public void click(int pos) {
                if (pos >= 0 && pos < addressList.size()) {
                    addressList.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
            }

            @Override
            public void select(int pos) {
                itemPos = pos;
                isItem = true;
                showListDialog();
            }
        });
    }

    private void doSave() {
        String nameStr = etName.getText().toString();
        String address1Str = etAddress1.getText().toString();
        String phoneNumberStr = etPhoneNumber.getText().toString();
        String emailStr = etEmail.getText().toString();
        String remarksStr = etRemarks.getText().toString();

        if (StringUtils.isEmpty(nameStr, address1Str)) {
            ToastUtils.showToast(getString(R.string.Incomplete_information));
        } else {
            if (!nameStr.equals(linkMan.getName())) {
                List<LinkMan> lml = DataSupport.where("name = ?", nameStr).find(LinkMan.class);
                if (lml != null && lml.size() > 0) {
                    ToastUtils.showToast(getString(R.string.linkman_name_repeat_warn));
                    return;
                }
            }

//            if (!StringUtils.isEmpty(phoneNumberStr)) {
//                if (!isMobileNO(phoneNumberStr)) {
//                    ToastUtils.showToast(getString(R.string.mobile_error));
//                    return;
//                }
//            }

            if (!StringUtils.isEmpty(emailStr)) {
                if (!isEmail(emailStr)) {
                    ToastUtils.showToast(getString(R.string.email_error));
                    return;
                }
            }

            if (addressList != null && addressList.size() > 0) {
                for (int i = 0; i < addressList.size(); i++) {
                    Log.i("sx", addressList.get(i).getCoinName() + "---" + addressList.get(i).getAddress());
                    if (StringUtils.isEmpty(addressList.get(i).getAddress())) {
                        ToastUtils.showToast(getString(R.string.Incomplete_information));
                        return;
                    } else {
                        addressList.get(i).setLinkManName(nameStr);
                    }
                }
            }

            Log.i("sx", currCoinName + "---" + address1Str);

            DataSupport.deleteAll(AddressBean.class, "linkManName = ?", linkMan.getName());

            AddressBean addressBean = new AddressBean();
            addressBean.setLinkManName(nameStr);
            addressBean.setCoinName(currCoinName);
            addressBean.setAddress(address1Str);
            addressBean.save();

            for (int i = 0; i < addressList.size(); i++) {
                AddressBean ab = new AddressBean();
                ab.setLinkManName(nameStr);
                ab.setCoinName(addressList.get(i).getCoinName());
                ab.setAddress(addressList.get(i).getAddress());
                ab.save();
            }

            if (!nameStr.equals(linkMan.getName())) {
                DataSupport.deleteAll(LinkMan.class, "name = ?", linkMan.getName());
                LinkMan linkMan = new LinkMan();
                linkMan.setName(nameStr);

                if (!StringUtils.isEmpty(phoneNumberStr)) {
                    linkMan.setPhoneNumber(phoneNumberStr);
                }

                if (!StringUtils.isEmpty(emailStr)) {
                    linkMan.setEmail(emailStr);
                }

                if (!StringUtils.isEmpty(remarksStr)) {
                    linkMan.setRemarks(remarksStr);
                }

                linkMan.setInitial(getLetter(nameStr));
                linkMan.save();
            } else {
                ContentValues values = new ContentValues();
                if (!StringUtils.isEmpty(phoneNumberStr)) {
                    values.put("phoneNumber", phoneNumberStr);
                } else {
                    values.put("phoneNumber", "");
                }
                if (!StringUtils.isEmpty(emailStr)) {
                    values.put("email", emailStr);
                } else {
                    values.put("email", "");
                }
                if (!StringUtils.isEmpty(remarksStr)) {
                    values.put("remarks", remarksStr);
                } else {
                    values.put("remarks", "");
                }
                DataSupport.updateAll(LinkMan.class, values, "name = ?", linkMan.getName());
            }

            ToastUtils.showToast(getString(R.string.save_success));
            finish();
        }
    }

    //手机号格式判断
//    private boolean isMobileNO(String mobiles) {
//        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
//        Matcher m = p.matcher(mobiles);
//        return m.matches();
//    }

    //邮箱格式判断
    private boolean isEmail(String email) {
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private void showDelDialog() {
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(MyApplication.getApp().getString(R.string.warm_prompt)).btnText(getString(R.string.dialog_cancel), getString(R.string.dialog_confirm))
                .titleTextColor(activity.getResources().getColor(R.color.black))
                .btnTextColor(getResources().getColor(R.color.dialog_cancel_txt), getResources().getColor(R.color.dialog_ok_txt))
                .content(MyApplication.getApp().getString(R.string.dialog_delete_link_man_title)).contentTextColor(getResources().getColor(R.color.font_sec_black))
                .setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                if (linkMan != null) {
                                    DataSupport.deleteAll(LinkMan.class, "name = ?", linkMan.getName());
                                    DataSupport.deleteAll(AddressBean.class, "linkManName = ?", linkMan.getName());
                                    ToastUtils.showToast(getString(R.string.delete_success));
                                    finish();
                                }
                                dialog.superDismiss();
                            }
                        });
        dialog.show();
    }


    //获取大写首字母
    private static String getLetter(String name) {
        String DefaultLetter = name.substring(0, 1);
        if (TextUtils.isEmpty(name)) {
            return DefaultLetter;
        }
        char char0 = name.toLowerCase().charAt(0);
        if (Character.isDigit(char0)) {
            return DefaultLetter;
        }
        ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
        if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
            HanziToPinyin.Token token = l.get(0);
            // toLowerCase()返回小写， toUpperCase()返回大写
            String letter = token.target.substring(0, 1).toUpperCase();
            char c = letter.charAt(0);
            // 这里的 'a' 和 'z' 要和letter的大小写保持一直。
            if (c < 'A' || c > 'Z') {
                return DefaultLetter;
            }
            return letter;
        }
        return DefaultLetter;
    }


}
