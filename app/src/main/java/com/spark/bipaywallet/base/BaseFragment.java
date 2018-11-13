package com.spark.bipaywallet.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spark.bipaywallet.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected View rootView;
    Unbinder unbinder;
    protected boolean isInit = false;
    protected boolean isNeedLoad = true;
    protected ImageView ivBack;
    protected TextView tvTitle;
    protected TextView tvGoto;
    protected LinearLayout llTitle;
    protected Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        activity = getActivity();
        tvTitle = rootView.findViewById(R.id.tvTitle);
        ivBack = rootView.findViewById(R.id.ivBack);
        llTitle = rootView.findViewById(R.id.llTitle);
        tvGoto = rootView.findViewById(R.id.tvGoto);
        isInit = true;
        initViews(savedInstanceState);
        initData();
        setListener();
    }

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

    protected abstract int getLayoutId();

    protected void initViews(Bundle savedInstanceState) {
    }

    protected void obtainData() {

    }


    protected void loadData() {
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


    public static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void displayLoadingPopup() {
        if (getActivity() != null) ((BaseActivity) getActivity()).displayLoadingPopup();
    }

    public void hideLoadingPopup() {
        if (getActivity() != null) ((BaseActivity) getActivity()).hideLoadingPopup();
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void onDestroyView() {
//        if (immersionBar != null) immersionBar.destroy();
        unbinder.unbind();
        super.onDestroyView();
    }

    protected void finish() {
        getActivity().finish();
    }

    public BaseActivity getmActivity() {
        return (BaseActivity) super.getActivity();
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
