package com.spark.bipaywallet.activity.importwallet;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.importwallet.fragment.ImportWalletBaseFragment;
import com.spark.bipaywallet.activity.importwallet.fragment.ImportWalletHelpWordFragment;
import com.spark.bipaywallet.activity.importwallet.fragment.ImportWalletPrivateKeyFragment;
import com.spark.bipaywallet.adapter.PagerAdapter;
import com.spark.bipaywallet.base.BaseTabFragmentActivity;
import com.spark.bipaywallet.ui.CustomViewPager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 导入钱包
 */

public class ImportWalletActivity extends BaseTabFragmentActivity implements ImportWalletBaseFragment.ImportWalletCallback {
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.vpPager)
    CustomViewPager vpPager;
    @BindArray(R.array.import_wallet_tab)
    String[] tabs;
    //    @BindView(R.id.tvLanguage)
//    TextView tvLanguage;
    @BindView(R.id.llDefault)
    LinearLayout llDefault;
    @BindView(R.id.llSuccess)
    LinearLayout llSuccess;
    @BindView(R.id.tvGoUse)
    TextView tvGoUse;
    private boolean isChina = true;
    private ImportWalletHelpWordFragment importWalletHelpWordFragment;
    private ImportWalletPrivateKeyFragment importWalletPrivateKeyFragment;


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_importwallet;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.fragment_one_pop_drqb));
        if (fragments.size() == 0) recoverFragment();
        vpPager.setOffscreenPageLimit(1);
        List<String> tabs = Arrays.asList(this.tabs);
        vpPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments, tabs));
        tab.setupWithViewPager(vpPager, true);

        tab.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tab, 50, 50);
            }
        });
    }

    @OnClick(R.id.tvGoUse)
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        finish();
    }

    @Override
    protected void setListener() {
        super.setListener();

        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 0) {
//                    tvLanguage.setVisibility(View.VISIBLE);
//                } else {
//                    tvLanguage.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void recoverFragment() {
        importWalletHelpWordFragment = (ImportWalletHelpWordFragment) getSupportFragmentManager()
                .findFragmentByTag(ImportWalletHelpWordFragment.TAG);
        importWalletPrivateKeyFragment = (ImportWalletPrivateKeyFragment) getSupportFragmentManager()
                .findFragmentByTag(ImportWalletPrivateKeyFragment.TAG);

        fragments.add(importWalletHelpWordFragment);
        fragments.add(importWalletPrivateKeyFragment);
    }

    @Override
    protected void initFragments() {
        if (importWalletHelpWordFragment == null)
            fragments.add(importWalletHelpWordFragment = ImportWalletHelpWordFragment.getInstance());
        if (importWalletPrivateKeyFragment == null)
            fragments.add(importWalletPrivateKeyFragment = ImportWalletPrivateKeyFragment.getInstance());
    }

    @Override
    public void success() {
        llDefault.setVisibility(View.GONE);
        llSuccess.setVisibility(View.VISIBLE);
//        tvLanguage.setVisibility(View.GONE);
    }

    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }


}
