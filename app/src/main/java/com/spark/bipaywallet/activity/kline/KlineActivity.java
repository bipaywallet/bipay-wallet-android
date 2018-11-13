package com.spark.bipaywallet.activity.kline;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.KChartView;
import com.github.tifezh.kchartlib.chart.MinuteChartView;
import com.github.tifezh.kchartlib.utils.ViewUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.adapter.MyPagerAdapter;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.DataParse;
import com.spark.bipaywallet.entity.KDataMessage;
import com.spark.bipaywallet.entity.KLineBean;
import com.spark.bipaywallet.entity.KNewOneData;
import com.spark.bipaywallet.entity.MinutesBean;
import com.spark.bipaywallet.ui.MyViewPager;
import com.spark.bipaywallet.ui.kchart.DataHelper;
import com.spark.bipaywallet.ui.kchart.KChartAdapter;
import com.spark.bipaywallet.ui.kchart.KLineEntity;
import com.spark.bipaywallet.ui.kchart.MinuteLineEntity;
import com.spark.bipaywallet.utils.DateUtils;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * 行情详情
 */

public class KlineActivity extends BaseActivity implements KlineContract.View, View.OnClickListener {
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindArray(R.array.k_line_tab)
    String[] titles;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.llAllTab)
    LinearLayout llAllTab;
    @BindView(R.id.tab)
    LinearLayout tab;
    @BindView(R.id.tvMore)
    TextView tvMore;

    private KChartView kChartView;
    private MinuteChartView minuteChartView;

    private ArrayList<TextView> textViews;
    private ArrayList<View> views;
    private TextView selectedTextView;
    private int type;
    private boolean isPopClick;
    private int childType = 0;
    private ProgressBar mProgressBar;
    private boolean isFirstLoad = true;
    private KChartAdapter kChartAdapter;
    private ArrayList<KLineBean> kLineDatas;
    private KNewOneData kNewOneData;
//    private MyCurrency mCurrency;
//    private List<MyCurrency> currencies = new ArrayList<>();

    private TextView maView;
    private TextView bollView;
    private TextView macdView;
    private TextView kdjView;
    private TextView rsiView;
    private TextView hideChildView;
    private TextView hideMainView;

    private Date startDate;
    private Date endDate;
    private String resolution;

    private String symbol = "";
    private KlineContract.Presenter presenter;

    @BindView(R.id.kCount)
    TextView kCount;
    @BindView(R.id.kUp)
    TextView kUp;
    @BindView(R.id.kLow)
    TextView kLow;

    @BindView(R.id.kDataText)
    TextView mDataText;
    @BindView(R.id.kDataOne)
    TextView mDataOne;
    @BindView(R.id.kRange)
    TextView kRange;

    public static void actionStart(Context context, String symbol) {
        Intent intent = new Intent(context, KlineActivity.class);
        intent.putExtra("symbol", symbol);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_market_details;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void obtainData() {
        new KlinePresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);

        symbol = getIntent().getStringExtra("symbol");
        getCurrent();

        textViews = new ArrayList<>();
        views = new ArrayList<>();

        List<String> titles = Arrays.asList(this.titles);
        if (titles != null) {
            initViewpager(titles);
            initTextView(5);
            initPopWindow(5);
        }

        selectedTextView = textViews.get(1);
        type = (int) selectedTextView.getTag();
        viewPager.setCurrentItem(1);
    }

    @OnClick({R.id.ivBack, R.id.llMore, R.id.llIndex})
    void setListener(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                return;
            case R.id.llMore:
                moreTabLayout.setVisibility(View.VISIBLE);
                indexLayout.setVisibility(View.GONE);
                break;
            case R.id.llIndex:
                moreTabLayout.setVisibility(View.GONE);
                indexLayout.setVisibility(View.VISIBLE);
                break;
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(llAllTab);
        }
    }

    /**
     * 获取头部信息
     */
    private void getCurrent() {
        HashMap<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("period", "1min");
        presenter.getNewKOneData(params);

//        OkhttpUtils.post().url(UrlFactory.getAllCurrency()).build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        List<MyCurrency> obj = new Gson().fromJson(response, new TypeToken<List<MyCurrency>>() {
//                        }.getType());
//                        currencies.clear();
//                        currencies.addAll(obj);
//                        setCurrentcy(currencies);
//                    }
//                });
    }

    /**
     * 头部显示内容
     *
     * @param objs
     */
//    private void setCurrentcy(List<MyCurrency> objs) {
//        try {
//            for (MyCurrency currency : objs) {
//                if (symbol.equals(currency.getSymbol())) {
//                    mCurrency = currency;
//                    break;
//                }
//            }
//
//            String strUp = String.valueOf(mCurrency.getHigh());
//            String strLow = String.valueOf(mCurrency.getLow());
//            String strCount = String.valueOf(mCurrency.getVolume());
//            Double douChg = mCurrency.getChg();
//            String strRang = MathUtils.getRundNumber(mCurrency.getChg() * 100, 2, "########0.") + "%";
//            String strDataText = "≈" + MathUtils.getRundNumber(mCurrency.getClose() * MainActivity.rate * mCurrency.getBaseUsdRate(),
//                    2, null) + "CNY";
//            String strDataOne = String.valueOf(mCurrency.getClose());
//            if (douChg < 0) {
//                mDataOne.setTextColor(getResources().getColor(R.color.chart_red));
//                kRange.setTextColor(getResources().getColor(R.color.chart_red));
//                kLandRange.setTextColor(getResources().getColor(R.color.chart_red));
//                kLandDataOne.setTextColor(getResources().getColor(R.color.chart_red));
//            } else {
//                mDataOne.setTextColor(getResources().getColor(R.color.kgreen));
//                kRange.setTextColor(getResources().getColor(R.color.kgreen));
//                kLandRange.setTextColor(getResources().getColor(R.color.kgreen));
//                kLandDataOne.setTextColor(getResources().getColor(R.color.kgreen));
//            }
//            kUp.setText(strUp);
//            kLow.setText(strLow);
//            kCount.setText(strCount);
//            kRange.setText(strRang);
//            mDataOne.setText(strDataOne);
//            mDataText.setText(strDataText);
//            kLandUp.setText(strUp);
//            kLandLow.setText(strLow);
//            kLandCount.setText(strCount);
//            kLandRange.setText(strRang);
//            kLandDataOne.setText(strDataOne);
//            kLandDataText.setText(strDataText);
//            if (!isStart) {
//                isStart = true;
//                startTCP();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 初始化viewpager
     *
     * @param titles
     */
    private void initViewpager(List<String> titles) {
        for (int i = 0; i < titles.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_kchartview, null);
            if (i == 0) {
                minuteChartView = view.findViewById(R.id.minuteChartView);
                minuteChartView.setVisibility(View.VISIBLE);
                RelativeLayout mLayout = view.findViewById(R.id.mLayout);
                mProgressBar = new ProgressBar(this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                        (ViewUtil.Dp2Px(this, 50), ViewUtil.Dp2Px(this, 50));
                lp.addRule(CENTER_IN_PARENT);
                mLayout.addView(mProgressBar, lp);
            } else {
                KChartView kChartView = view.findViewById(R.id.kchart_view);
                initKchartView(kChartView);
                kChartView.setVisibility(View.VISIBLE);
                kChartView.setAdapter(new KChartAdapter());
            }
            views.add(view);
        }
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(views);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPagerView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置kchartview
     *
     * @param kChartView
     */
    private void initKchartView(KChartView kChartView) {
        kChartView.setCandleSolid(true);
        kChartView.setGridRows(4);
        kChartView.setGridColumns(4);
        kChartView.setOnSelectedChangedListener(new BaseKChartView.OnSelectedChangedListener() {
            @Override
            public void onSelectedChanged(BaseKChartView view, Object point, int index) {
                KLineEntity data = (KLineEntity) point;
//                Log.i("onSelectedChanged", "index:" + index + " closePrice:" + data.getClosePrice());
            }
        });
    }

    /**
     * viewpager和textview的点击事件
     */
    private void setPagerView() {
        for (int j = 0; j < textViews.size(); j++) {
            textViews.get(j).setSelected(false);
            int tag = (int) textViews.get(j).getTag();
            if (tag == type) {

                if (isPopClick) {
                    tvMore.setText(selectedTextView.getText());
                    tvMore.setSelected(true);
                } else {
                    tvMore.setText(getString(R.string.more));
                    tvMore.setSelected(false);
                    textViews.get(j).setSelected(true);
                }
                textViews.get(j).setSelected(true);

                View view = views.get(j);
                if (type != GlobalConstant.TAG_DIVIDE_TIME) {
                    kChartView = view.findViewById(R.id.kchart_view);
                    kChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                    kChartView.setChidType(childType);
                    kChartAdapter = (KChartAdapter) kChartView.getAdapter();
                    if (kChartAdapter.getDatas() == null || kChartAdapter.getDatas().size() == 0) {
                        loadCurData();
                    }
                } else {
                    minuteChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                    if (isFirstLoad)
                        loadCurData();
                }
            } else if (!isPopClick) {
                tvMore.setSelected(false);
            }
        }
    }

    /**
     * 设置tab栏显示内容
     *
     * @param count
     */
    private void initTextView(int count) {
        List<String> titles = Arrays.asList(this.titles);
        for (int i = 0; i < titles.size(); i++) {
            if (i < count) {
                View popTextView = LayoutInflater.from(this).inflate(R.layout.textview_pop, null);
                TextView textView = popTextView.findViewById(R.id.tvPop);
                LinearLayout tvLayout = popTextView.findViewById(R.id.tvLayout);
                tvLayout.removeAllViews();
                textView.setText(titles.get(i));
                textView.setTag(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPopClick = false;
                        selectedTextView = (TextView) view;
                        int selectedTag = (int) selectedTextView.getTag();
                        type = selectedTag;
                        viewPager.setCurrentItem(selectedTag);
                    }
                });
                textViews.add(textView);
                tab.addView(textView);
            }
        }
    }

    private PopupWindow popupWindow;
    private LinearLayout moreTabLayout;
    private LinearLayout indexLayout;

    /**
     * 初始化popwindow
     *
     * @param count
     */
    private void initPopWindow(int count) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_kline_popwindow, null);
        initPopChidView(contentView);
        intMoreTab(count);
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
    }

    /**
     * 初始化popwindow里的控件
     *
     * @param contentView
     */
    private void initPopChidView(View contentView) {
        moreTabLayout = contentView.findViewById(R.id.tabPop);
        indexLayout = contentView.findViewById(R.id.llIndex);
        maView = contentView.findViewById(R.id.tvMA);
        maView.setSelected(true);
        maView.setOnClickListener(this);
        bollView = contentView.findViewById(R.id.tvBOLL);
        bollView.setOnClickListener(this);
        macdView = contentView.findViewById(R.id.tvMACD);
        kdjView = contentView.findViewById(R.id.tvKDJ);
        rsiView = contentView.findViewById(R.id.tvRSI);
        hideMainView = contentView.findViewById(R.id.tvMainHide);
        hideMainView.setOnClickListener(this);
        macdView = contentView.findViewById(R.id.tvMACD);
        macdView.setSelected(true);
        macdView.setOnClickListener(this);
        kdjView = contentView.findViewById(R.id.tvKDJ);
        kdjView.setOnClickListener(this);
        rsiView = contentView.findViewById(R.id.tvRSI);
        rsiView.setOnClickListener(this);
        hideChildView = contentView.findViewById(R.id.tvChildHide);
        hideChildView.setSelected(false);
        hideChildView.setOnClickListener(this);
    }

    /**
     * 设置more显示内容
     *
     * @param count
     */
    private void intMoreTab(int count) {
        List<String> titles = Arrays.asList(this.titles);
        for (int i = count; i < titles.size(); i++) {
            View popTextView = LayoutInflater.from(this).inflate(R.layout.textview_pop, null);
            TextView textView = popTextView.findViewById(R.id.tvPop);
            LinearLayout tvLayout = popTextView.findViewById(R.id.tvLayout);
            tvLayout.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(ViewUtil.Dp2Px(this, 20), 0, 0, 0);
            textView.setText(titles.get(i));
            textView.setTag(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPopClick = true;
                    selectedTextView = (TextView) view;
                    int selectedTag = (int) selectedTextView.getTag();
                    type = selectedTag;
                    viewPager.setCurrentItem(selectedTag);
                    popupWindow.dismiss();
                }
            });
            moreTabLayout.addView(textView);
            textViews.add(textView);
        }
    }


    private void loadCurData() {
        if (type != GlobalConstant.TAG_DIVIDE_TIME)
            kChartView.showLoading();
        else
            mProgressBar.setVisibility(View.VISIBLE);
        Long to = System.currentTimeMillis();
        endDate = DateUtils.getDate("HH:mm", to);
        Long from = to;
//        LogUtils.logi(getPackageName(), "type==" + type);

        String period = "min";

        switch (type) {
            case GlobalConstant.TAG_DIVIDE_TIME:
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY) - 1;
                c.set(Calendar.HOUR_OF_DAY, hour);
                String strDate = DateUtils.getFormatTime("HH:mm", c.getTime());
                startDate = DateUtils.getDateTransformString(strDate, "HH:mm");
                resolution = 1 + "";
                String str = DateUtils.getFormatTime(null, c.getTime());
                from = DateUtils.getTimeMillis(null, str);
                period = "min";
                break;
            case GlobalConstant.TAG_ONE_MINUTE:
                from = to - 24L * 60 * 60 * 1000;//前一天数据
                resolution = 1 + "";
                period = "1min";
                break;
            case GlobalConstant.TAG_FIVE_MINUTE:
                from = to - 2 * 24L * 60 * 60 * 1000;//前两天数据
                resolution = 5 + "";
                period = "5min";
                break;
            case GlobalConstant.TAG_THIRTY_MINUTE:
                from = to - 12 * 24L * 60 * 60 * 1000; //前12天数据
                resolution = 30 + "";
                period = "30min";
                break;
            case GlobalConstant.TAG_AN_HOUR:
                from = to - 24 * 24L * 60 * 60 * 1000;//前 24天数据
                resolution = 1 + "H";
                period = "60min";
                break;
            case GlobalConstant.TAG_DAY:
                from = to - 60 * 24L * 60 * 60 * 1000; //前60天数据
                resolution = 1 + "D";
                period = "1day";
                break;
            case GlobalConstant.TAG_WEEK:
                from = to - 730 * 24L * 60 * 60 * 1000; //前两年数据
                resolution = 1 + "W";
                period = "1week";
                break;
            case GlobalConstant.TAG_MONTH:
                from = to - 1095 * 24L * 60 * 60 * 1000; //前三年数据
                resolution = 1 + "M";
                period = "1mon";
                break;
        }

        Log.i("sx", "kkkkk");

        HashMap<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
//        params.put("from", from + "");
//        params.put("to", to + "");
//        params.put("resolution", resolution);
        params.put("period", period);
        presenter.KData(params);
    }

    /**
     * 副图的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvMA:
            case R.id.tvBOLL:
            case R.id.tvMainHide:
                if (view.getId() == R.id.tvMA) {
                    maView.setSelected(true);
                    bollView.setSelected(false);
                    hideMainView.setSelected(false);
                } else if (view.getId() == R.id.tvBOLL) {
                    maView.setSelected(false);
                    bollView.setSelected(true);
                    hideMainView.setSelected(false);
                } else {
                    maView.setSelected(false);
                    bollView.setSelected(false);
                    hideMainView.setSelected(true);
                }
                if (type == GlobalConstant.TAG_DIVIDE_TIME) {
                    minuteChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                } else {
                    kChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                }
                popupWindow.dismiss();
                break;
            case R.id.tvMACD:
            case R.id.tvRSI:
            case R.id.tvKDJ:
            case R.id.tvChildHide:
                if (view.getId() == R.id.tvMACD) {
                    childType = 0;
                    macdView.setSelected(true);
                    rsiView.setSelected(false);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(false);
                } else if (view.getId() == R.id.tvKDJ) {
                    childType = 1;
                    macdView.setSelected(false);
                    rsiView.setSelected(false);
                    kdjView.setSelected(true);
                    hideChildView.setSelected(false);
                } else if (view.getId() == R.id.tvRSI) {
                    childType = 2;
                    macdView.setSelected(false);
                    rsiView.setSelected(true);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(false);
                } else {
                    childType = -1;
                    macdView.setSelected(false);
                    rsiView.setSelected(false);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(true);
                }
                if (type == GlobalConstant.TAG_DIVIDE_TIME) {
                } else {
                    kChartView.setChidType(childType);
                }
                popupWindow.dismiss();
                break;
        }
    }


    @Override
    public void setPresenter(KlineContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void KDataFail(Integer code, String toastMessage) {
        mProgressBar.setVisibility(View.GONE);
        kChartView.refreshEnd();

        if (!StringUtils.isEmpty(toastMessage))
            ToastUtils.showToast(toastMessage);
    }

    @Override
    public void KDataSuccess(Object obj) {
        if (obj == null) {
            mProgressBar.setVisibility(View.GONE);
            kChartView.refreshEnd();
            return;
        }

        KDataMessage kDataMessage = (KDataMessage) obj;

        DataParse kData = new DataParse();
        switch (type) {
            case GlobalConstant.TAG_DIVIDE_TIME: // 分时图
                mProgressBar.setVisibility(View.GONE);
                try {
//                    kData.parseMinutes(kDataMessage.getKline_data(), (float) mCurrency.getLastDayClose());
                    kData.parseMinutes(kDataMessage.getKline_data());
                    ArrayList<MinutesBean> objList = kData.getDatas();
                    if (objList != null && objList.size() > 0) {
                        ArrayList<MinuteLineEntity> minuteLineEntities = new ArrayList<>();
                        for (int i = 0; i < objList.size(); i++) {
                            MinuteLineEntity minuteLineEntity = new MinuteLineEntity();
                            MinutesBean minutesBean = objList.get(i);
                            minuteLineEntity.setAvg(minutesBean.getAvprice()); // 成交价
                            minuteLineEntity.setPrice(minutesBean.getCjprice());
                            minuteLineEntity.setTime(DateUtils.getDateTransformString(minutesBean.getTime(), "HH:mm"));
                            minuteLineEntity.setVolume(minutesBean.getCjnum());
                            minuteLineEntity.setClose(minutesBean.getClose());
                            minuteLineEntities.add(minuteLineEntity);
                        }
                        if (isFirstLoad) { // 避免界面重绘
                            DataHelper.calculateMA30andBOLL(minuteLineEntities);
                            minuteChartView.initData(minuteLineEntities,
                                    startDate,
                                    endDate,
                                    null,
                                    null,
//                                    (float) mCurrency.getLow(),
                                    kNewOneData == null ? 0.0f : kNewOneData.getLow(),
                                    maView.isSelected());
                            isFirstLoad = false;
                        }
                    }
                } catch (Exception e) {
                    LogUtils.logi("tag", getString(R.string.parse_error));
                    Log.i("sx", "kkkkk1");
                }
                break;
            default:
                try {
                    kData.parseKLine(kDataMessage.getKline_data(), type);
                    kLineDatas = kData.getKLineDatas();
                    if (kLineDatas != null && kLineDatas.size() > 0) {
                        ArrayList<KLineEntity> kLineEntities = new ArrayList<>();
                        for (int i = 0; i < kLineDatas.size(); i++) {
                            KLineEntity lineEntity = new KLineEntity();
                            KLineBean kLineBean = kLineDatas.get(i);
                            lineEntity.setDate(kLineBean.getDate());
                            lineEntity.setOpen(kLineBean.getOpen());
                            lineEntity.setClose(kLineBean.getClose());
                            lineEntity.setHigh(kLineBean.getHigh());
                            lineEntity.setLow(kLineBean.getLow());
                            lineEntity.setVolume(kLineBean.getVol());
                            kLineEntities.add(lineEntity);
                        }
                        LogUtils.logi("kChartAdapter.getDatas().size()", kChartAdapter.getDatas().size() + "");
                        kChartAdapter.addFooterData(DataHelper.getALL(this, kLineEntities));
                        kChartView.startAnimation();
                        kChartView.refreshEnd();
                    } else {
                        kChartView.refreshEnd();
                    }
                } catch (Exception e) {
                    LogUtils.logi("tag", getString(R.string.parse_error));
                    Log.i("sx", "kkkkk2");
                }

                break;
        }
    }

    @Override
    public void getNewKOneDataFail(Integer code, String toastMessage) {
        if (!StringUtils.isEmpty(toastMessage))
            ToastUtils.showToast(toastMessage);
    }

    @Override
    public void getNewKOneDataSuccess(Object obj) {
        if (obj == null) return;

        kNewOneData = (KNewOneData) obj;
        kUp.setText(kNewOneData.getHigh() + "");
        kLow.setText(kNewOneData.getLow() + "");
    }


}
