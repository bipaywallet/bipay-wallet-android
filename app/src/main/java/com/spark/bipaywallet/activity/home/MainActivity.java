package com.spark.bipaywallet.activity.home;

import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.aboutus.AboutUsActivity;
import com.spark.bipaywallet.activity.addCoin.AddCoinActivity;
import com.spark.bipaywallet.activity.createwallet.CreateWalletActivity;
import com.spark.bipaywallet.activity.exchange.ExchangeActivity;
import com.spark.bipaywallet.activity.help.HelpActivity;
import com.spark.bipaywallet.activity.importwallet.ImportWalletActivity;
import com.spark.bipaywallet.activity.linkman.LinkManActivity;
import com.spark.bipaywallet.activity.message.MessageActivity;
import com.spark.bipaywallet.activity.payment.PaymentActivity;
import com.spark.bipaywallet.activity.selectlanguage.SelectLanguageActivity;
import com.spark.bipaywallet.activity.setting.SettingActivity;
import com.spark.bipaywallet.activity.walletdetails.WalletDetailsActivity;
import com.spark.bipaywallet.activity.walletmanage.WalletManageActivity;
import com.spark.bipaywallet.activity.zxing.ZxingActivity;
import com.spark.bipaywallet.adapter.HomeWalletAdapter;
import com.spark.bipaywallet.adapter.SideBarAdapter;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.CoinTypeEnum;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.MyCoin;
import com.spark.bipaywallet.entity.MyCurrencyData;
import com.spark.bipaywallet.entity.MyETHRecord;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.entity.SideBarBean;
import com.spark.bipaywallet.entity.TokenContract;
import com.spark.bipaywallet.entity.TokenQuery;
import com.spark.bipaywallet.entity.VersionMessage;
import com.spark.bipaywallet.entity.Wallet;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.ui.CustomScrollView;
import com.spark.bipaywallet.utils.CommonUtils;
import com.spark.bipaywallet.utils.DpPxUtils;
import com.spark.bipaywallet.utils.MathUtils;
import com.spark.bipaywallet.utils.NetCodeUtils;
import com.spark.bipaywallet.utils.PermissionUtils;
import com.spark.bipaywallet.utils.ShareUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.spark.bipaywallet.utils.VersionCompareUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主页
 */

public class MainActivity extends BaseActivity implements MainContract.View {
    @BindView(R.id.dlRoot)
    DrawerLayout dlRoot;
    @BindView(R.id.lltop)
    LinearLayout lltop;
    @BindView(R.id.ivSwitch)
    ImageView ivSwitch;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    @BindView(R.id.rvSideWallet)
    RecyclerView rvSideWallet;
    @BindView(R.id.rvHome)
    RecyclerView rvHome;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tvTopMoneyLeft)
    TextView tvTopMoneyLeft;
    @BindView(R.id.llDefaultWallet)
    LinearLayout llDefaultWallet;
    @BindView(R.id.llNoWallet)
    LinearLayout llNoWallet;
    @BindView(R.id.ivEye)
    ImageView ivEye;
    @BindView(R.id.tvTotalTag)
    TextView tvTotalTag;
    @BindView(R.id.scrollView)
    CustomScrollView scrollView;

    private MainContract.Presenter presenter;
    private Wallet currentWallet;
    private MyHandler myHandler;

    private SideBarAdapter adapter;
    private List<SideBarBean> list;
    private HomeWalletAdapter homeWalletAdapter;
    private List<MyCoin> myCoinList;
    private List<MyCurrencyData> myCurrencyDataList = new ArrayList<>();

    private String currAllMoney;
    private boolean isEyeOpen = true;
    private int moneyCode = 1;
    private long lastPressTime = 0;
    private String downLoadUrl;
    private String strNewVersion;
    private boolean isCheckClick = false;

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    startSys();
                    break;
                case GlobalConstant.PERMISSION_STORAGE:
                    String filename = getString(R.string.app_name) + "_" + strNewVersion + ".apk";
                    CommonUtils.showUpDialog(MainActivity.this, downLoadUrl, filename);
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    ToastUtils.showToast(getString(R.string.camera_permission));
                    break;
                case GlobalConstant.PERMISSION_STORAGE:
                    ToastUtils.showToast(getString(R.string.storage_permission));
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler.sendEmptyMessageDelayed(1, 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myHandler != null) myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (lastPressTime == 0 || now - lastPressTime > 2 * 1000) {
            ToastUtils.showToast(getString(R.string.exit_again));
            lastPressTime = now;
        } else if (now - lastPressTime < 2 * 1000) super.onBackPressed();

    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        ivSwitch.setVisibility(View.VISIBLE);
        ivMore.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.home_tab_wallet));
        new MainPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        myHandler = new MyHandler(this);
        initSlide();
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {// 避免嵌套ScrollView滑动卡顿的问题
                return false;
            }
        };
        rvHome.setLayoutManager(manager);
        myCoinList = new ArrayList<>();
        homeWalletAdapter = new HomeWalletAdapter(this, R.layout.item_home_wallet, myCoinList);
        rvHome.setAdapter(homeWalletAdapter);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_coin, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        homeWalletAdapter.setEmptyView(emptyView);
    }

    /**
     * 设置侧滑栏
     */
    private void initSlide() {
        int languageCode = SharedPreferenceInstance.getInstance().getLanguageCode();
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) rvSideWallet.getLayoutParams();
        if (languageCode == 1) {
            llp.width = DpPxUtils.dip2px(getApplicationContext(), 120);
        } else {
            llp.width = DpPxUtils.dip2px(getApplicationContext(), 120);
        }
        rvSideWallet.setLayoutParams(llp);

        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvSideWallet.setLayoutManager(manager);
        list = initList();
        adapter = new SideBarAdapter(activity, R.layout.item_sidebar, list);
        rvSideWallet.setAdapter(adapter);
    }

    private List<SideBarBean> initList() {
        List<SideBarBean> list = new ArrayList<>();
        SideBarBean itemWallet = new SideBarBean(getString(R.string.fragment_four_wallet), 1, GlobalConstant.TAG_QBGL);
        SideBarBean itemScan = new SideBarBean(getString(R.string.wallet_pop_scan), 2, GlobalConstant.TAG_SYS);
        SideBarBean itemMan = new SideBarBean(getString(R.string.fragment_four_man), 3, GlobalConstant.TAG_LXR);
        SideBarBean itemMsg = new SideBarBean(getString(R.string.fragment_four_message), 4, GlobalConstant.TAG_XXZX);
        SideBarBean itemCoin = new SideBarBean(getString(R.string.fragment_four_coin), 5, GlobalConstant.TAG_HBSZ);
        SideBarBean itemLanguage = new SideBarBean(getString(R.string.fragment_four_language), 6, GlobalConstant.TAG_YYSZ);
        SideBarBean itemShare = new SideBarBean(getString(R.string.fragment_four_share_app), 7, GlobalConstant.TAG_SHARE);
        SideBarBean itemSet = new SideBarBean(getString(R.string.fragment_four_setting), 8, GlobalConstant.TAG_XTSZ);

        SideBarBean itemHelp = new SideBarBean(getString(R.string.fragment_four_help), 8.1f, GlobalConstant.TAG_BZZX);
        SideBarBean itemAbout = new SideBarBean(getString(R.string.fragment_four_aboutus), 8.2f, GlobalConstant.TAG_GYWM);
        SideBarBean itemVersion = new SideBarBean(getString(R.string.fragment_four_version), 8.3f, GlobalConstant.TAG_BBGX);

        itemHelp.setParentItem(itemSet);
        itemAbout.setParentItem(itemSet);
        itemVersion.setParentItem(itemSet);

        list.add(itemWallet);
        list.add(itemScan);
        list.add(itemMan);
        list.add(itemMsg);
        list.add(itemCoin);
        list.add(itemLanguage);
        list.add(itemSet);
        list.add(itemShare);

        list.add(itemHelp);
        list.add(itemAbout);
        list.add(itemVersion);
        return list;
    }

    @OnClick({R.id.ivSwitch, R.id.ivMore, R.id.lltop, R.id.ivCloseSide, R.id.ivEye, R.id.llAddMoney,
            R.id.tvCreate, R.id.tvImport})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.ivSwitch:
                showActivity(ExchangeActivity.class, null);
                break;
            case R.id.ivEye:
                switchSee();
                break;
            case R.id.lltop:
                Bundle bundle = new Bundle();
                bundle.putString("walletName", currentWallet.getName());
                showActivity(WalletDetailsActivity.class, bundle);
                break;
            case R.id.ivMore:
                dlRoot.openDrawer(Gravity.END);
                break;
            case R.id.ivCloseSide:
                dlRoot.closeDrawers();
                break;
            case R.id.llAddMoney:
                showActivity(AddCoinActivity.class, null);
                break;
            case R.id.tvCreate:
                showActivity(CreateWalletActivity.class, null);
                break;
            case R.id.tvImport:
                showActivity(ImportWalletActivity.class, null);
                break;
        }
    }

    /**
     * 切换是否可见
     */
    private void switchSee() {
        if (isEyeOpen) {
            ivEye.setImageResource(R.mipmap.icon_eye_close);
            isEyeOpen = !isEyeOpen;
            tvTopMoneyLeft.setText("****");
            homeWalletAdapter.setShow(false);
            homeWalletAdapter.notifyDataSetChanged();
        } else {
            ivEye.setImageResource(R.mipmap.icon_eye_open);
            isEyeOpen = !isEyeOpen;
            tvTopMoneyLeft.setText(MathUtils.getBigDecimalRundNumber(currAllMoney, 2));
            homeWalletAdapter.setShow(true);
            homeWalletAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        scrollView.setOnScrollChangedListener(new CustomScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                if (y <= 0) {
                    recoveryTop();
                } else if (y >= DpPxUtils.dip2px(MainActivity.this, 160)) {  //滑动到下面
                    setTitle(getString(R.string.fragment_one_pop_all_money)
                            + getMoneyName()
                            + " " + MathUtils.getBigDecimalRundNumber(currAllMoney, 2));
                }
            }
        });

        homeWalletAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("myCoin", myCoinList.get(position));
                showActivity(PaymentActivity.class, bundle);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLoadData();
            }
        });

        adapter.setSideBarListener(new SideBarAdapter.SideBarListener() {
            @Override
            public void onSideBarItemClick(SideBarBean item, int position) {
                switch (item.getTag()) {
                    case GlobalConstant.TAG_QBGL: // 钱包管理
                        showActivity(WalletManageActivity.class, null);
                        break;
                    case GlobalConstant.TAG_SYS: // 扫一扫
                        if (!PermissionUtils.isCanUseCamera(activity))
                            checkPermission(GlobalConstant.PERMISSION_CAMERA, Permission.CAMERA);
                        else {
                            startSys();
                        }
                        break;
                    case GlobalConstant.TAG_LXR: // 联系人
                        showActivity(LinkManActivity.class, null);
                        break;
                    case GlobalConstant.TAG_HBSZ: // 货币设置
                        showActivity(SettingActivity.class, null);
                        break;
                    case GlobalConstant.TAG_BBGX: // 版本更新
                        isCheckClick = true;
                        presenter.getVersionMessage();
                        break;
                    case GlobalConstant.TAG_GYWM: // 关于我们
                        showActivity(AboutUsActivity.class, null);
                        break;
                    case GlobalConstant.TAG_BZZX: // 帮助中心
                        showActivity(HelpActivity.class, null);
                        break;
                    case GlobalConstant.TAG_XXZX: // 消息中心
                        showActivity(MessageActivity.class, null);
                        break;
                    case GlobalConstant.TAG_YYSZ: // 语言设置
                        showActivity(SelectLanguageActivity.class, null);
                        break;
                    case GlobalConstant.TAG_SHARE: // 分享APP
                        ShareUtils.shareText(MainActivity.this, "", "http://app.bipay.io/appDownload.html");
                        break;
                    case GlobalConstant.TAG_XTSZ:
                        return;
                }
                dlRoot.closeDrawers();
            }
        });
    }

    private void startSys() {
        showActivity(ZxingActivity.class, null);
    }

    @Override
    protected void loadData() {
        super.loadData();
        presenter.getVersionMessage();
        refreshLoadData();
    }

    private void refreshLoadData() {
        currentWallet = MyApplication.getApp().getCurrentWallet();
        if (currentWallet == null) {
            List<Wallet> walletList = DataSupport.findAll(Wallet.class);
            if (walletList != null && walletList.size() > 0) {
                MyApplication.getApp().setCurrentWallet(walletList.get(0));
//                SharedPreferenceInstance.getInstance().setCurrentWalletPos(0);
                currentWallet = MyApplication.getApp().getCurrentWallet();
            } else {
//                SharedPreferenceInstance.getInstance().setCurrentWalletPos(-1);
            }
        }

        moneyCode = SharedPreferenceInstance.getInstance().getMoneyCode();
        tvTotalTag.setText(getString(R.string.fragment_one_pop_all_money) + getMoneyName());
        if (currentWallet != null) {
            setTitle(currentWallet.getName());
            showOrHideWalletLayout(true);
            List<MyCoin> list = DataSupport.where("walletName = ? and isAdded = ?",
                    currentWallet.getName(), "1").find(MyCoin.class);
            if (list != null && list.size() > 0) {
                myCoinList.clear();
                myCoinList.addAll(list);
                sort(myCoinList);
                homeWalletAdapter.notifyDataSetChanged();
                setAllMoneyText();

                for (int i = 0; i < myCoinList.size(); i++) {
                    MyCoin myCoin = myCoinList.get(i);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("address", myCoin.getAddress());
                    params.put("coinName", myCoin.getName());//注意此参数是用来拼接url的，不是传给接口的
                    if (myCoin.getCoinType() == 0 || myCoin.getCoinType() == 206 || myCoin.getCoinType() == 207 ||
                            myCoin.getCoinType() == 2 || myCoin.getCoinType() == 208 || myCoin.getCoinType() == 145) {
                        presenter.getCoinMessage(params);
                    } else if (myCoin.getCoinType() == 60) {
                        if (myCoin.getName() != null && myCoin.getName().equals("ETH")) {
                            presenter.getETHMessage(params);

                            HashMap<String, String> tokenParams = new HashMap<>();
                            tokenParams.put("address", myCoin.getAddress());
                            tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                            tokenParams.put("start", "0");//查询高度开始位置
                            presenter.transactionETHRecord(tokenParams);
                        } else {
//                            HashMap<String, String> tokenParams = new HashMap<>();
//                            tokenParams.put("address", myCoin.getAddress());
//                            tokenParams.put("contractAddress", myCoin.getContractAddress());
//                            tokenParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
//                            presenter.getTokenMessage(tokenParams);

                            HashMap<String, String> contractParams = new HashMap<>();
                            contractParams.put("address", myCoin.getContractAddress());
                            contractParams.put("ethAddress", myCoin.getAddress());
                            contractParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                            presenter.getTokenContract(contractParams);
                        }
                    }
                }
                presenter.getCurrencyDataList();
            } else {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                myCoinList.clear();
                homeWalletAdapter.notifyDataSetChanged();

                currAllMoney = "0.00";
                if (isEyeOpen) {
                    tvTopMoneyLeft.setText("----");
                } else {
                    tvTopMoneyLeft.setText("****");
                }
            }
        } else {
            showOrHideWalletLayout(false);
        }

        if (MyApplication.getApp().isChangeWallet()) {
            recoveryTop();
            scrollView.smoothScrollTo(0, 0);
            MyApplication.getApp().setChangeWallet(false);
        }
    }

    private void recoveryTop() {
        if (currentWallet != null) {
            setTitle(currentWallet.getName());
        }
    }

    private String getMoneyName() {
        if (moneyCode == 1) {
            return "(CNY)";
        } else {
            return "(USD)";
        }
    }

    private void showOrHideWalletLayout(boolean isShow) {
        if (isShow) {
            llTitle.setVisibility(View.VISIBLE);
            llDefaultWallet.setVisibility(View.VISIBLE);
            llNoWallet.setVisibility(View.GONE);
        } else {
            llTitle.setVisibility(View.GONE);
            llDefaultWallet.setVisibility(View.GONE);
            llNoWallet.setVisibility(View.VISIBLE);
        }
    }

    private void setAllMoneyText() {
        String allMoney = "0";
        for (int k = 0; k < myCoinList.size(); k++) {
            MyCoin myCoin = myCoinList.get(k);

            if (moneyCode == 1) {
                allMoney = MathUtils.getBigDecimalAdd(allMoney, MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getRate(), 8), 8);
            } else {
                allMoney = MathUtils.getBigDecimalAdd(allMoney, MathUtils.getBigDecimalMultiply(myCoin.getNum(), myCoin.getUsdRate(), 8), 8);
            }
        }
        currAllMoney = allMoney;

        if (isEyeOpen && tvTopMoneyLeft != null) {
            tvTopMoneyLeft.setText(MathUtils.getBigDecimalRundNumber(currAllMoney, 2));
        } else if (tvTopMoneyLeft != null) {
            tvTopMoneyLeft.setText("****");
        }
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getCoinMessageSuccess(HttpCoinMessage obj, String coinName) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;

        if (currentWallet != null) {
            for (int i = 0; i < myCoinList.size(); i++) {
                if (myCoinList.get(i).getAddress().equals(obj.getAddress()) &&
                        StringUtils.isNotEmpty(myCoinList.get(i).getName()) &&
                        myCoinList.get(i).getName().equals(coinName)) {
                    ContentValues values = new ContentValues();
                    values.put("num", MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8));
                    DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?", obj.getAddress(), coinName);

                    myCoinList.get(i).setNum(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow8(), 8));
                    homeWalletAdapter.notifyDataSetChanged();

                    setAllMoneyText();
                }
            }
        }
    }

    @Override
    public void getCoinMessageFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getETHMessageSuccess(HttpETHMessage obj, String coinName) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;
        if (currentWallet != null) {
            for (int i = 0; i < myCoinList.size(); i++) {
                if (myCoinList.get(i).getAddress().equals(obj.getAddress()) &&
                        StringUtils.isNotEmpty(myCoinList.get(i).getName()) &&
                        myCoinList.get(i).getName().equals("ETH")) {
                    ContentValues values = new ContentValues();
                    values.put("num", MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8));
                    DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?", obj.getAddress(), coinName);

                    myCoinList.get(i).setNum(MathUtils.getBigDecimalDivide(obj.getTotalAmount(), MathUtils.getBigDecimal10Pow18(), 8));
                    homeWalletAdapter.notifyDataSetChanged();

                    setAllMoneyText();
                }
            }
        }
    }

    @Override
    public void getETHMessageFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getTokenMessageSuccess(Object obj, String contractAddress) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;

        HttpETHMessage message = (HttpETHMessage) obj;

        if (currentWallet != null) {
            for (int i = 0; i < myCoinList.size(); i++) {
                if (myCoinList.get(i).getContractAddress() != null
                        && myCoinList.get(i).getContractAddress().equals(contractAddress)
                        && myCoinList.get(i).getAddress().equals(message.getAddress())) {
                    ContentValues values = new ContentValues();
                    values.put("num",
                            MathUtils.getBigDecimalDivide(message.getTotalAmount(),
                                    MathUtils.getBigDecimalPow("10", myCoinList.get(i).getDecimals(), 8), 8));
                    DataSupport.updateAll(MyCoin.class, values, "address = ? and contractAddress = ?",
                            message.getAddress(), contractAddress);

                    myCoinList.get(i).setNum(MathUtils.getBigDecimalDivide(message.getTotalAmount(),
                            MathUtils.getBigDecimalPow("10", myCoinList.get(i).getDecimals(), 8), 8));
                    homeWalletAdapter.notifyDataSetChanged();

                    setAllMoneyText();
                }
            }
        }
    }

    @Override
    public void getTokenMessageFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getTokenContractSuccess(Object obj, String ethAddress) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;

        TokenContract tokenContract = (TokenContract) obj;

        List<MyCoin> mclist = DataSupport.where("address = ? and contractAddress = ?",
                ethAddress, tokenContract.getContractAddress()).find(MyCoin.class);

        if (mclist != null || mclist.size() > 0) {
            ContentValues values = new ContentValues();
            if (StringUtils.isNotEmpty(tokenContract.getName())) {
                values.put("name", tokenContract.getName());
            }
            values.put("decimals", MathUtils.getBigDecimalRundNumber(tokenContract.getDecimals(), 0));
            values.put("rate", MathUtils.getBigDecimalRundNumber(tokenContract.getCnyRate(), 2));
            values.put("usdRate", MathUtils.getBigDecimalRundNumber(tokenContract.getUstRate(), 2));
            values.put("logoUrl", tokenContract.getImgUrl());
            DataSupport.updateAll(MyCoin.class, values, "address = ? and contractAddress = ?",
                    ethAddress, tokenContract.getContractAddress());

//            myCoinList.add(DataSupport.where("address = ? and contractAddress = ?",
//                    ethAddress, tokenContract.getContractAddress()).find(MyCoin.class).get(0));

            for (int z = 0; z < myCoinList.size(); z++) {
                if (myCoinList.get(z).getContractAddress() != null &&
                        myCoinList.get(z).getContractAddress().equals(tokenContract.getContractAddress()) &&
                        myCoinList.get(z).getAddress().equals(ethAddress)) {
                    if (StringUtils.isNotEmpty(tokenContract.getName())) {
                        myCoinList.get(z).setName(tokenContract.getName());
                    }
                    myCoinList.get(z).setDecimals(Integer.valueOf(MathUtils.getBigDecimalRundNumber(tokenContract.getDecimals(), 0)));
                    myCoinList.get(z).setRate(MathUtils.getBigDecimalRundNumber(tokenContract.getCnyRate(), 2));
                    myCoinList.get(z).setUsdRate(MathUtils.getBigDecimalRundNumber(tokenContract.getUstRate(), 2));
                    myCoinList.get(z).setLogoUrl(tokenContract.getImgUrl());
                }
            }

            homeWalletAdapter.notifyDataSetChanged();

            HashMap<String, String> params = new HashMap<>();
            params.put("address", ethAddress);
            params.put("contractAddress", tokenContract.getContractAddress());
            params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
            presenter.getTokenMessage(params);
        }
    }

    @Override
    public void getTokenContractFail(Integer code, String message) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

        if (code == -1) {
            String[] strs = message.split(":");
            if (strs.length > 1) {
                HashMap<String, String> contractParams = new HashMap<>();
                contractParams.put("address", strs[0]);
                contractParams.put("ethAddress", strs[1]);
                contractParams.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                presenter.getTokenQuery(contractParams);
            }
        } else {
            NetCodeUtils.checkedErrorCode(this, code, message);
        }
    }

    @Override
    public void getTokenQuerySuccess(Object obj, String ethAddress) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;

        TokenQuery tokenQuery = (TokenQuery) obj;

        List<MyCoin> mclist = DataSupport.where("address = ? and contractAddress = ?",
                ethAddress, tokenQuery.getAddress()).find(MyCoin.class);

        if (mclist != null || mclist.size() > 0) {
            ContentValues values = new ContentValues();
            if (StringUtils.isNotEmpty(tokenQuery.getSymbol())) {
                values.put("name", tokenQuery.getSymbol());
            }
            values.put("decimals", MathUtils.getBigDecimalRundNumber(tokenQuery.getDecimals(), 0));
            DataSupport.updateAll(MyCoin.class, values, "address = ? and contractAddress = ?",
                    ethAddress, tokenQuery.getAddress());

            for (int z = 0; z < myCoinList.size(); z++) {
                if (myCoinList.get(z).getContractAddress() != null &&
                        myCoinList.get(z).getContractAddress().equals(tokenQuery.getAddress()) &&
                        myCoinList.get(z).getAddress().equals(ethAddress)) {
                    if (StringUtils.isNotEmpty(tokenQuery.getSymbol())) {
                        myCoinList.get(z).setName(tokenQuery.getSymbol());
                    }
                    myCoinList.get(z).setDecimals(Integer.valueOf(MathUtils.getBigDecimalRundNumber(tokenQuery.getDecimals(), 0)));
                }
            }

            homeWalletAdapter.notifyDataSetChanged();

            HashMap<String, String> params = new HashMap<>();
            params.put("address", ethAddress);
            params.put("contractAddress", tokenQuery.getAddress());
            params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
            presenter.getTokenMessage(params);
        }
    }

    @Override
    public void getTokenQueryFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void transactionETHRecordSuccess(Object obj) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;

        List<MyETHRecord> myETHRecordList = (List<MyETHRecord>) obj;
        MyETHRecord myETHRecord = myETHRecordList.get(0);

        List<MyETHRecord.ETHNormalTransaction> ethNormalTransactionList = myETHRecord.getNormalTransactions();

        for (int i = 0; i < ethNormalTransactionList.size(); i++) {
            if (ethNormalTransactionList.get(i).getContractAddress() != null) {
                List<MyCoin> mclist = DataSupport.where("address = ? and contractAddress = ?",
                        myETHRecord.getAddress(), ethNormalTransactionList.get(i).getContractAddress()).find(MyCoin.class);

                if (mclist == null || mclist.size() == 0) {
                    List<MyCoin> ethCoinlist = DataSupport.where("address = ? and name = ?",
                            myETHRecord.getAddress(), "ETH").find(MyCoin.class);

                    if (ethCoinlist != null && ethCoinlist.size() > 0) {
                        MyCoin myCoin = new MyCoin();
                        myCoin.setWalletName(currentWallet.getName());
                        myCoin.setAddress(myETHRecord.getAddress());
                        myCoin.setCoinType(60);
                        myCoin.setAdded(true);
                        myCoin.setContractAddress(ethNormalTransactionList.get(i).getContractAddress());
                        myCoin.setSubPrivKey(ethCoinlist.get(0).getSubPrivKey());
                        myCoin.setName("---");

                        myCoin.save();
                        myCoinList.add(myCoin);
                        sort(myCoinList);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("address", ethNormalTransactionList.get(i).getContractAddress());
                        params.put("ethAddress", myETHRecord.getAddress());
                        params.put("coinName", "eth");//注意此参数是用来拼接url的，不是传给接口的
                        presenter.getTokenContract(params);
                    }
                }
            }
        }
    }

    @Override
    public void transactionETHRecordFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getCurrencyDataListSuccess(List<MyCurrencyData> obj) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

        if (obj == null) return;

        myCurrencyDataList = obj;

        if (currentWallet != null) {
            for (int i = 0; i < myCoinList.size(); i++) {
                String usdtRMBRate = "6.8";
                for (int j = 0; j < myCurrencyDataList.size(); j++) {
                    if (myCurrencyDataList.get(j).getName().equalsIgnoreCase(myCoinList.get(i).getName())) {

                        ContentValues values = new ContentValues();
                        values.put("rate", MathUtils.getBigDecimalRundNumber(myCurrencyDataList.get(j).getClose_rmb(), 8));
                        values.put("usdRate", MathUtils.getBigDecimalRundNumber(myCurrencyDataList.get(j).getClose(), 8));
                        DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?", myCoinList.get(i).getAddress(), myCoinList.get(i).getName());

                        myCoinList.get(i).setRate(MathUtils.getBigDecimalRundNumber(myCurrencyDataList.get(j).getClose_rmb(), 8));
                        myCoinList.get(i).setUsdRate(MathUtils.getBigDecimalRundNumber(myCurrencyDataList.get(j).getClose(), 8));
//                        myCoinList.get(i).setLogoUrl(myCurrencyDataList.get(j).getLogo_url());
                    }
                }

                if (StringUtils.isNotEmpty(myCoinList.get(i).getName()) && myCoinList.get(i).getName().equals("DVC")) {
                    ContentValues values = new ContentValues();
                    values.put("rate", "1.0");
                    values.put("usdRate", "0.147");
                    DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?", myCoinList.get(i).getAddress(), myCoinList.get(i).getName());

                    myCoinList.get(i).setRate("1.0");
                    myCoinList.get(i).setUsdRate("0.147");
                }

                if (StringUtils.isNotEmpty(myCoinList.get(i).getName()) && myCoinList.get(i).getName().equals("USDT")) {
                    if (myCurrencyDataList.size() > 0) {
                        usdtRMBRate = MathUtils.getBigDecimalDivide(myCurrencyDataList.get(0).getClose_rmb(), myCurrencyDataList.get(0).getClose(), 8);
                    }

                    ContentValues values = new ContentValues();
                    values.put("rate", MathUtils.getBigDecimalRundNumber(usdtRMBRate, 8));
                    values.put("usdRate", "1");
                    DataSupport.updateAll(MyCoin.class, values, "address = ? and name = ?", myCoinList.get(i).getAddress(), myCoinList.get(i).getName());

                    myCoinList.get(i).setRate(MathUtils.getBigDecimalRundNumber(usdtRMBRate, 8));
                    myCoinList.get(i).setUsdRate("1");
                }

                homeWalletAdapter.notifyDataSetChanged();

                setAllMoneyText();
            }

            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler.sendEmptyMessageDelayed(1, 30000);
            }
        }
    }

    @Override
    public void getCurrencyDataListFail(Integer code, String toastMessage) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        NetCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getVersionMessageSuccess(VersionMessage obj) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (obj == null) return;
        downLoadUrl = obj.getAndroid().getDownload();
        strNewVersion = obj.getAndroid().getCode();
        doSuccess();
    }

    @Override
    public void getVersionMessageFail(Integer code, String toastMessage) {
        if (isCheckClick) {
            ToastUtils.showToast(getString(R.string.version_no_new));
            isCheckClick = false;
        }
    }

    private void doSuccess() {
        if (StringUtils.isNotEmpty(CommonUtils.getVersionName()) &&
                StringUtils.isNotEmpty(strNewVersion)) {
            if (VersionCompareUtil.compareVersion(strNewVersion, CommonUtils.getVersionName()) == 1) {
                if (!PermissionUtils.isCanUseStorage(this)) {
                    checkPermission(GlobalConstant.PERMISSION_STORAGE, Permission.STORAGE);
                } else {
                    String filename = getString(R.string.save_app_version_name) + "_" + strNewVersion + ".apk";
                    CommonUtils.showUpDialog(this, downLoadUrl, filename);
                }
            } else {
                if (isCheckClick) {
                    ToastUtils.showToast(getString(R.string.version_no_new));
                    isCheckClick = false;
                }
            }
        }
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    @Override
    public void getNoticeMessageSuccess(List<Notice> notices) {

    }

    @Override
    public void getNoticeMessageFail(Integer code, String toastMessage) {

    }

    /**
     * Handler 当作定时器来使用
     */
    private static class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity mainActivity) {
            mActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();

            if (Build.VERSION.SDK_INT >= 17) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
            } else {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
            }

            if (msg.what == 1 && activity != null) {
                activity.refreshLoadData();
            }
        }
    }

    public void sort(List<MyCoin> myCoinList) {
        //根据币种order进行排序，代币没有order值，放在最后。返回正整数位置排在后，返回负整数位置排在前，返回0位置不变
        Collections.sort(myCoinList, new Comparator<MyCoin>() {
            @Override
            public int compare(MyCoin lhs, MyCoin rhs) {
                if (CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()) != null &&
                        CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()) != null) {
                    return CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()).getOrder() - CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()).getOrder();
                } else if (CoinTypeEnum.getCoinTypeEnumByName(lhs.getName()) != null) {
                    return -1;
                } else if (CoinTypeEnum.getCoinTypeEnumByName(rhs.getName()) != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }


}
