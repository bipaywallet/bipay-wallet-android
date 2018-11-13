package com.spark.bipaywallet.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.spark.bipaysdk.jni.JNIUtil;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;
import com.spark.bipaywallet.utils.KeyboardUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    protected ImageView ivBack;
    protected TextView tvTitle;
    protected TextView tvGoto;
    protected LinearLayout llTitle;
    protected Activity activity;
    private PopupWindow loadingPopup;
    private Unbinder unbinder;
    protected ImmersionBar immersionBar;
    protected boolean isNeedhide = true;
    protected JNIUtil jniUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage();
        setContentView(getActivityLayoutId());
        unbinder = ButterKnife.bind(this);
        activity = this;
        ActivityManage.addActivity(this);
        initBaseView();
        initPop();
        initViews(savedInstanceState);
        initData();
        obtainData();
        setListener();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 初始化语言
     */
    private void initLanguage() {
        Locale l = null;
        int code = SharedPreferenceInstance.getInstance().getLanguageCode();
        if (code == 1) l = Locale.CHINESE;
        else if (code == 2) l = Locale.ENGLISH;

        Resources resources = getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(l);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            getApplicationContext().createConfigurationContext(config);
        }
        Locale.setDefault(l);
        resources.updateConfiguration(config, dm);
    }

    /**
     * 获取布局ID
     */
    protected abstract int getActivityLayoutId();

    /**
     * 初始化工作
     *
     * @param savedInstanceState
     */
    protected void initViews(Bundle savedInstanceState) {
    }

    /**
     * 获取本地或传递的数据
     */
    protected void obtainData() {
    }


    /**
     * 初始数据加载
     */
    protected void loadData() {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        ActivityManage.removeActivity(this);
        hideLoadingPopup();
        if (immersionBar != null) immersionBar.destroy();
    }

    /**
     * 显示加载框
     */
    public void displayLoadingPopup() {
        if (!loadingPopup.isShowing())
            loadingPopup.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 隐藏加载框
     */
    public void hideLoadingPopup() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }
    }

    /**
     * 设置基础化控件
     */
    private void initBaseView() {
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        llTitle = findViewById(R.id.llTitle);
        tvGoto = findViewById(R.id.tvGoto);
    }

    private void initPop() {
        View loadingView = getLayoutInflater().inflate(R.layout.pop_loading, null);
        loadingPopup = new PopupWindow(loadingView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingPopup.setFocusable(true);
        loadingPopup.setClippingEnabled(false);
        loadingPopup.setBackgroundDrawable(new ColorDrawable());
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    public void setShowBackBtn(boolean showBackBtn) {
        if (ivBack != null && showBackBtn) {
            if (showBackBtn) {
                ivBack.setVisibility(View.VISIBLE);
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            } else {
                ivBack.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 处理软件盘智能弹出和隐藏
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                if (isNeedhide) {
                    KeyboardUtils.editKeyboard(ev, view, this);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 各控件的点击事件
     *
     * @param v
     */
    protected void setOnClickListener(View v) {

    }

    /**
     * 各控件的点击
     */
    protected void setListener() {

    }

    /**
     * 跳转activity,不关闭当前界面
     *
     * @param cls
     * @param bundle
     */
    protected void showActivity(Class<?> cls, Bundle bundle) {
        showActivity(cls, bundle, -1);
    }

    /**
     * 跳转activity,不关闭当前界面，含跳转回来的的回调
     *
     * @param cls
     * @param bundle
     */
    protected void showActivity(Class<?> cls, Bundle bundle, int requesCode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        if (requesCode >= 0)
            startActivityForResult(intent, requesCode);
        else
            startActivity(intent);
    }

}
