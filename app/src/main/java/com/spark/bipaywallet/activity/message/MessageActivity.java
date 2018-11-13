package com.spark.bipaywallet.activity.message;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.notice.NoticeActivity;
import com.spark.bipaywallet.adapter.MessageAdapter;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.Notice;
import com.spark.bipaywallet.entity.SendParam;
import com.spark.bipaywallet.ui.MyLoadMoreView;
import com.spark.bipaywallet.utils.NetCodeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 消息中心
 */

public class MessageActivity extends BaseActivity implements MessageContract.View {
    @BindView(R.id.rvMessage)
    RecyclerView rvMessage;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private MessageAdapter adapter;
    private List<Notice> noticeList = new ArrayList<>();
    private int page = 1;
    private MessageContract.Presenter presenter;
    private MyHandler myHandler;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        new MessagePresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        myHandler = new MyHandler(this);
        setTitle(getString(R.string.activity_message_title));
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvMessage.setLayoutManager(manager);
        adapter = new MessageAdapter(this, R.layout.item_message, noticeList);
        rvMessage.setAdapter(adapter);
    }

    @Override
    protected void loadData() {
        getNoticeMessage(true);
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
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, rvMessage);
        adapter.setLoadMoreView(new MyLoadMoreView());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    private void refresh() {
        adapter.setEnableLoadMore(false);
        page = 1;
        getNoticeMessage(false);
    }

    private void loadMore() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setEnabled(false);
            page++;
            getNoticeMessage(false);
        }
    }

    private void getNoticeMessage(boolean isShow) {
        if (isShow) {
            displayLoadingPopup();
        }
        SendParam sendParam = new SendParam();
        sendParam.setPageIndex(page);
        sendParam.setPageSize(10);
        sendParam.setSortFields("id");
        Gson gson = new Gson();
        presenter.getNoticeMessage(gson.toJson(sendParam));
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getNoticeMessageSuccess(final List<Notice> notices) {
        Message message = new Message();
        message.what = 1;
        message.obj = notices;
        myHandler.sendMessage(message);
    }

    @Override
    public void doPostFail(final Integer code, final String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void refreshLoadData(Object object) {
        hideLoadingPopup();
        List<Notice> obj = (List<Notice>) object;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
        }
        if (adapter != null) {
            adapter.setEnableLoadMore(true);
        } else {
            return;
        }
        if (obj != null && obj.size() > 0) {
            if (page == 1) {
                adapter.setNewData(obj);
            } else {
                adapter.addData(obj);
                adapter.loadMoreComplete();
            }
        } else {
            if (page == 1) {
                View emptyView = getLayoutInflater().inflate(R.layout.empty_no_osmsg, null);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                ((TextView) emptyView.findViewById(R.id.tvMessage)).setText(getString(R.string.empty_no_osmsg_title));
                adapter.setEmptyView(emptyView);
            } else {
                adapter.loadMoreEnd(true);
            }
        }
    }

    private void fail(Bundle bundle) {
        hideLoadingPopup();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
        }
        NetCodeUtils.checkedErrorCode(MessageActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    private static class MyHandler extends Handler {

        private final WeakReference<MessageActivity> mActivity;

        private MyHandler(MessageActivity messageActivity) {
            mActivity = new WeakReference<>(messageActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessageActivity activity = mActivity.get();

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
