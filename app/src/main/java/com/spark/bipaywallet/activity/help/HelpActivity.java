package com.spark.bipaywallet.activity.help;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.notice.NoticeActivity;
import com.spark.bipaywallet.adapter.HelpAdapter;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.entity.SendParam;
import com.spark.bipaywallet.utils.NetCodeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 帮助中心
 */

public class HelpActivity extends BaseActivity implements HelpContract.View {
    @BindView(R.id.recyclerView)
    RecyclerView rvHelp;
    private HelpAdapter adapter;
    private List<Notice> noticeList = new ArrayList<>();
    private HelpContract.Presenter presenter;
    private MyHandler myHandler;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_global_recycleview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.help_center));
        new HelpPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        myHandler = new MyHandler(this);
        initRv();
    }

    @Override
    protected void loadData() {
        getHelpMessage();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvHelp.setLayoutManager(manager);
        adapter = new HelpAdapter(this, R.layout.item_coin_key, noticeList);
        rvHelp.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("content", ((Notice) adapter.getItem(position)).getContent());
                showActivity(NoticeActivity.class, bundle);
            }
        });
    }

    private void getHelpMessage() {
        displayLoadingPopup();
        SendParam sendParam = new SendParam();
        sendParam.setPageIndex(1);
        sendParam.setPageSize(20);
        sendParam.setSortFields("id");
        Gson gson = new Gson();
        presenter.getHelpMessage(gson.toJson(sendParam));
    }

    @Override
    public void setPresenter(HelpContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getHelpMessageSuccess(List<Notice> notices) {
        Message message = new Message();
        message.what = 1;
        message.obj = notices;
        myHandler.sendMessage(message);
    }

    @Override
    public void doPostFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void refreshLoadData(Object obj) {
        hideLoadingPopup();
        List<Notice> notices = (List<Notice>) obj;
        if (notices != null && notices.size() > 0) {
            noticeList.clear();
            noticeList.addAll(notices);
            adapter.notifyDataSetChanged();
        } else {
            View emptyView = getLayoutInflater().inflate(R.layout.empty_no_osmsg, null);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            ((TextView) emptyView.findViewById(R.id.tvMessage)).setText(getString(R.string.empty_no_message_title));
            adapter.setEmptyView(emptyView);
        }
    }

    private void fail(Bundle bundle) {
        hideLoadingPopup();
        NetCodeUtils.checkedErrorCode(HelpActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    private static class MyHandler extends Handler {

        private final WeakReference<HelpActivity> mActivity;

        private MyHandler(HelpActivity helpActivity) {
            mActivity = new WeakReference<>(helpActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HelpActivity activity = mActivity.get();

            if (Build.VERSION.SDK_INT >= 17) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
            } else {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
            }

            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.refreshLoadData(msg.obj);
                        break;
                    case -1:
                        activity.fail(msg.getData());
                        break;
                }
            }
        }
    }


}
