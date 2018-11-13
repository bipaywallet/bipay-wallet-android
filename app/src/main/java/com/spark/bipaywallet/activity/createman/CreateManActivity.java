package com.spark.bipaywallet.activity.createman;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.EditTextAdapter;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.entity.AddressBean;
import com.spark.bipaywallet.entity.LinkMan;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.ui.MyCaptureActivity;
import com.spark.bipaywallet.utils.HanziToPinyin;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.PermissionUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

import static com.spark.bipaywallet.activity.transferpay.TransferPayActivity.REQUEST_CODE;

/**
 * 新建联系人
 */

public class CreateManActivity extends BaseActivity {
    @BindView(R.id.rvAddress)
    RecyclerView rvAddress;
    @BindArray(R.array.currency_str)
    String[] currencyStrs;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etAddress1)
    EditText etAddress1;
    @BindView(R.id.tvSave)
    TextView tvSave;
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etRemarks)
    EditText etRemarks;
    @BindView(R.id.ivScan)
    ImageView ivScan;
    @BindView(R.id.tvCoinType)
    TextView tvCoinType;
    private EditTextAdapter adapter;
    private ArrayList<AddressBean> addressList;
    private List<String> coinNameList = new ArrayList<>();
    private String currCoinName = "BTC";
    private NormalListDialog normalDialog = null;
    private boolean isItem;
    private int itemPos;


    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    Intent intent = new Intent(CreateManActivity.this, MyCaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    ToastUtils.showToast(getString(R.string.camera_permission));
                    break;
            }
        }
    };

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_create_man;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        tvGoto.setVisibility(View.GONE);
        ivScan.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_create_man_title));
        addressList = new ArrayList<>();
        tvCoinType.setText(currencyStrs[0]);
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
    }

    @OnClick({R.id.ivScan, R.id.ivAddress, R.id.tvCoinType, R.id.tvSave})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.ivScan:
                if (!PermissionUtils.isCanUseCamera(CreateManActivity.this)) {
                    checkPermission(GlobalConstant.PERMISSION_CAMERA, Permission.CAMERA);
                } else {
                    Intent intent = new Intent(CreateManActivity.this, MyCaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
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
                isItem = true;
                itemPos = pos;
                showListDialog();

            }
        });
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

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
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

            List<LinkMan> lml = DataSupport.where("name = ?", nameStr).find(LinkMan.class);
            if (lml != null && lml.size() > 0) {
                ToastUtils.showToast(getString(R.string.linkman_name_repeat_warn));
                return;
            }

            if (addressList != null && addressList.size() > 0) {
                for (int i = 0; i < addressList.size(); i++) {
                    LogUtils.i(addressList.get(i).getCoinName() + "---" + addressList.get(i).getAddress());
                    if (StringUtils.isEmpty(addressList.get(i).getAddress())) {
                        ToastUtils.showToast(getString(R.string.Incomplete_information));
                        return;
                    } else {
                        addressList.get(i).setLinkManName(nameStr);
                    }
                }
            }
            LogUtils.i(currCoinName + "---" + address1Str);
            AddressBean addressBean = new AddressBean();
            addressBean.setLinkManName(nameStr);
            addressBean.setCoinName(currCoinName);
            addressBean.setAddress(address1Str);
            List<AddressBean> addressBeanList = new ArrayList<>();
            addressBeanList.add(addressBean);
            addressBeanList.addAll(addressList);
            DataSupport.saveAll(addressBeanList);

            if (addressBeanList.size() > 0) {
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
                ToastUtils.showToast(getString(R.string.save_success));
                finish();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);

                        if (!StringUtils.isEmpty(result)) {
                            String[] strs = result.split(":");

                            List<MyCoin> coinList = null;
                            if (strs.length > 1) {
                                coinList = DataSupport.where("name = ?", strs[0].toUpperCase()).find(MyCoin.class);
                            }

                            if (StringUtils.isEmpty(etAddress1.getText().toString())) {
                                if (coinList != null && coinList.size() > 0 && strs.length > 1) {
                                    int index = coinNameList.indexOf(strs[0]);
                                    etAddress1.setText(strs[1]);
                                    if (index >= 0) {
                                        currCoinName = coinNameList.get(index);
                                        tvCoinType.setText(currCoinName);
                                    }
                                } else {
                                    etAddress1.setText(result);
                                }
                            } else {
                                if (addressList != null && addressList.size() > 0) {
                                    boolean isChange = false;
                                    for (int i = 0; i < addressList.size(); i++) {
                                        if (StringUtils.isEmpty(addressList.get(i).getAddress())) {
                                            if (coinList != null && coinList.size() > 0 && strs.length > 1) {
                                                addressList.get(i).setAddress(strs[1]);
                                                if (coinNameList.indexOf(strs[0]) >= 0) {
                                                    addressList.get(i).setCoinName(strs[0]);
                                                }
                                            } else {
                                                addressList.get(i).setAddress(result);
                                            }

                                            adapter.notifyDataSetChanged();
                                            isChange = true;
                                            break;
                                        }
                                    }

                                    if (!isChange) {
                                        AddressBean addressBean = new AddressBean();
                                        if (coinList != null && coinList.size() > 0 && strs.length > 1) {
                                            addressBean.setCoinName(strs[0]);
                                            addressBean.setAddress(strs[1]);
                                        } else {
                                            addressBean.setCoinName(coinNameList.get(0));
                                            addressBean.setAddress(result);
                                        }
                                        addressList.add(addressBean);
                                        adapter.notifyItemInserted(addressList.size() - 1);
                                    }
                                } else {
                                    AddressBean addressBean = new AddressBean();
                                    if (coinList != null && coinList.size() > 0 && strs.length > 1) {
                                        addressBean.setCoinName(strs[0]);
                                        addressBean.setAddress(strs[1]);
                                    } else {
                                        addressBean.setCoinName(coinNameList.get(0));
                                        addressBean.setAddress(result);
                                    }
                                    addressList.add(addressBean);
                                    adapter.notifyItemInserted(addressList.size() - 1);
                                }
                            }
                        } else {
                            ToastUtils.showToast(getString(R.string.zxing_fail));
                        }
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        ToastUtils.showToast(getString(R.string.zxing_fail));
                    }
                }
                break;
        }
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
