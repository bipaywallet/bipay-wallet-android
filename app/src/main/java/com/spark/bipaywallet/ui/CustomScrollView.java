package com.spark.bipaywallet.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
    private boolean isIntercept = true;
    private OnScrollChangeListener onScrollChangedListener;

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept && super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(x, y, oldx, oldy);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangeListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }
}
