package com.spark.bipaywallet.activity.notice;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.BindView;

/**
 * 公告详情
 */

public class NoticeActivity extends BaseActivity {
    @BindView(R.id.wb)
    WebView wb;
    private String content;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_notice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.activity_notice));
        initWb();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            content = bundle.getString("content");
            Log.i("sx", content);

            String CSS_STYLE = "<style>* {color:#FFFFFF;}</style>";
            if (StringUtils.isNotEmpty(content)) {
                wb.loadDataWithBaseURL(null, CSS_STYLE + getNewContent(content), "text/html", "utf-8", null);
            }
        }
    }

    private void initWb() {
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setSupportZoom(false);
        wb.getSettings().setBuiltInZoomControls(false);
        wb.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wb.setVerticalScrollBarEnabled(false);
        wb.setHorizontalScrollBarEnabled(false);
        wb.setBackgroundColor(getResources().getColor(R.color.bg_create_man));
    }

    /**
     * 将html文本内容中包含img标签的图片，宽度变为屏幕宽度，高度根据宽度比例自适应
     **/
    public String getNewContent(String htmltext) {
        try {
            Document doc = Jsoup.parse(htmltext);
            Elements elements = doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("width", "100%").attr("height", "auto");
            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }

}
