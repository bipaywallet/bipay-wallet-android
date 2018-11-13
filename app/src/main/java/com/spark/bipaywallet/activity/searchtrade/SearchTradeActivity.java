package com.spark.bipaywallet.activity.searchtrade;


import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.utils.StringUtils;

import butterknife.BindView;

/**
 * 公告详情
 */

public class SearchTradeActivity extends BaseActivity {
    @BindView(R.id.wb)
    WebView wb;
    private String url;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_search_trade;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_trade));
        initWb();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = getIntent().getStringExtra("url");
            if (StringUtils.isNotEmpty(url)) {
                wb.loadUrl(url);
            }
        }
    }

    private void initWb() {
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setSupportZoom(false);
        wb.getSettings().setBuiltInZoomControls(false);
        wb.setBackgroundColor(getResources().getColor(R.color.white));
        wb.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
