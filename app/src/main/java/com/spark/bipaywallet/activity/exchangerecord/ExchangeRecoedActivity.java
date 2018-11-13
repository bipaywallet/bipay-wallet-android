package com.spark.bipaywallet.activity.exchangerecord;


import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.exchangedetail.ExchangeDetailActivity;
import com.spark.bipaywallet.adapter.ExchangeRecordAdapter;
import com.spark.bipaywallet.app.MyApplication;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.config.Injection;
import com.spark.bipaywallet.entity.MyExchangeRecord;
import com.spark.bipaywallet.utils.NetCodeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * 兑换历史
 */

public class ExchangeRecoedActivity extends BaseActivity implements ExchangeRecordContract.View {
    @BindView(R.id.rvMessage)
    RecyclerView rvRecord;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private MyHandler myHandler;
    private ExchangeRecordContract.Presenter presenter;
    private List<MyExchangeRecord> recordList = new ArrayList<>();
    private ExchangeRecordAdapter adapter;

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh(false);
    }

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
        setTitle(getString(R.string.activity_exchange_record_title));
        new ExchangeRecordPresenter(Injection.provideTasksRepository(this.getApplicationContext()), this);
        myHandler = new MyHandler(this);
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rvRecord.setLayoutManager(manager);

        List<MyExchangeRecord> list = DataSupport.where("walletName = ?",
                MyApplication.getApp().getCurrentWallet().getName()).find(MyExchangeRecord.class);

        if (list != null && list.size() > 0) {
            recordList.addAll(list);
            sort(recordList);
        }

        adapter = new ExchangeRecordAdapter(this, R.layout.adapter_exchange_record, recordList);
        rvRecord.setAdapter(adapter);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_no_wallet, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(emptyView);
    }

    @Override
    protected void loadData() {
        refresh(true);
    }

    @Override
    protected void setListener() {
        super.setListener();

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("id", recordList.get(position).getExchangeId());
                showActivity(ExchangeDetailActivity.class, bundle);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(false);
            }
        });
    }

    private void refresh(boolean isShow) {
        List<MyExchangeRecord> list = DataSupport.where("walletName = ?",
                MyApplication.getApp().getCurrentWallet().getName()).find(MyExchangeRecord.class);

        if (list != null && list.size() > 0) {
            recordList.clear();
            recordList.addAll(list);
            sort(recordList);
            adapter.notifyDataSetChanged();
        } else {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getStatus().equals(getString(R.string.case_exchange_waiting)) ||
                    recordList.get(i).getStatus().equals(getString(R.string.case_exchange_confirming)) ||
                    recordList.get(i).getStatus().equals(getString(R.string.case_exchange_exchanging)) ||
                    recordList.get(i).getStatus().equals(getString(R.string.case_exchange_sending))) {
                if (isShow) {
                    displayLoadingPopup();
                }
                presenter.getExchangeStatus(recordList.get(i).getExchangeId(), getExchangeStatusJson(recordList.get(i).getExchangeId()));
            } else {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }

    private String getExchangeStatusJson(String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id", "test");
            jsonObject.put("method", "getStatus");

            JSONObject jo = new JSONObject();
            jo.put("id", id);
            jsonObject.put("params", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void setPresenter(ExchangeRecordContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getExchangeStatusSuccess(String id, Object obj) {
        if (obj == null) return;
        Message message = new Message();
        message.what = 1;
        final Bundle bundle = new Bundle();
        bundle.putString("exchangeId", id);
        bundle.putString("status", (String) obj);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void exchangeStatusSuccess(Bundle bundle) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        hideLoadingPopup();

        ContentValues values = new ContentValues();
        values.put("status", bundle.getString("status"));
        DataSupport.updateAll(MyExchangeRecord.class, values, "walletName = ? and exchangeId = ?",
                MyApplication.getApp().getCurrentWallet().getName(), bundle.getString("exchangeId"));

        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getExchangeId().equals(bundle.getString("exchangeId"))) {
                recordList.get(i).setStatus(bundle.getString("status"));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getExchangeStatusFail(Integer code, String toastMessage) {
        Message message = new Message();
        message.what = -1;
        final Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("toastMessage", toastMessage);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void fail(Bundle bundle) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        hideLoadingPopup();
        NetCodeUtils.checkedErrorCode(ExchangeRecoedActivity.this, bundle.getInt("code"), bundle.getString("toastMessage"));
    }

    private static class MyHandler extends Handler {

        private final WeakReference<ExchangeRecoedActivity> mActivity;

        private MyHandler(ExchangeRecoedActivity helpActivity) {
            mActivity = new WeakReference<>(helpActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExchangeRecoedActivity activity = mActivity.get();

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
                        activity.exchangeStatusSuccess(msg.getData());
                        break;
                    case -1:
                        activity.fail(msg.getData());
                        break;
                }
            }
        }
    }

    //时间排序
    public void sort(List<MyExchangeRecord> myRecordBeanList) {
        // 排序
        Collections.sort(myRecordBeanList, new Comparator<MyExchangeRecord>() {
            @Override
            public int compare(MyExchangeRecord lhs, MyExchangeRecord rhs) {
                return -lhs.getTime().compareTo(rhs.getTime());
            }
        });
    }


}
