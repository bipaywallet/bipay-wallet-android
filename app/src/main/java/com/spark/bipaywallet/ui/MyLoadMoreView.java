package com.spark.bipaywallet.ui;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.spark.bipaywallet.R;

public class MyLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.loading_no;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.noView;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.noView;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.noView;
    }
}
