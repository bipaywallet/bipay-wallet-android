package com.spark.bipaywallet.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class MyPagerAdapter extends android.support.v4.view.PagerAdapter {
    private List<View> views;

    public MyPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "标题" + position;
    }
}
