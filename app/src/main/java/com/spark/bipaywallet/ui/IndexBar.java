package com.spark.bipaywallet.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexBar extends View {
    private static final String TAG = "zxt/IndexBar";
    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};//#在最后面（默认的数据源）
    private List<String> mIndexDatas;//索引数据源
    private boolean isNeedRealIndex;//是否需要根据实际的数据来生成索引数据源（例如 只有 A B C 三种tag，那么索引栏就 A B C 三项）

    private int mWidth, mHeight;//View的宽
    private int mGapHeight;//每个index区域的高度

    private Paint mPaint;

    private int mPressedBackground;//手指按下时的背景色

    //以下边变量是外部set进来的
    private TextView mPressedShowTextView;//用于特写显示正在被触摸的index值
    private List<String> mSourceDatas;//Adapter的数据源
    private LinearLayoutManager mLayoutManager;

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());//默认的TextSize
        mPressedBackground = Color.TRANSPARENT;
        if (!isNeedRealIndex) {//不需要真实的索引数据源
            mIndexDatas = Arrays.asList(INDEX_STRING);
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.WHITE);

        //设置index触摸监听器
        setmOnIndexPressedListener(new onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mPressedShowTextView != null) { //显示hintTexView
                    mPressedShowTextView.setVisibility(View.VISIBLE);
                    mPressedShowTextView.setText(text);
                }
                //滑动Rv
                if (mLayoutManager != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                //隐藏hintTextView
                if (mPressedShowTextView != null) {
                    mPressedShowTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取出宽高的MeasureSpec  Mode 和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = 0, measureHeight = 0;//最终测量出来的宽高

        //得到合适宽度：
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容

        for (int i = 0; i < mIndexDatas.size(); i++) {
            index = mIndexDatas.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
            measureWidth = Math.max(indexBounds.width(), measureWidth);//循环结束后，得到index的最大宽度
            measureHeight = Math.max(indexBounds.width(), measureHeight);//循环结束后，得到index的最大高度，然后*size
        }
        measureHeight *= mIndexDatas.size();
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, wSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        //得到合适的高度：
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, hSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mIndexDatas.size() > 0) {
            mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexDatas.size();
        }

        int t = getPaddingTop();//top的基准点(支持padding)
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容

        for (int i = 0; i < mIndexDatas.size(); i++) {
            index = mIndexDatas.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();//获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
            int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);//计算出在每格index区域，竖直居中的baseLine值
            canvas.drawText(index, mWidth / 2 - indexBounds.width() / 2, t + mGapHeight * i + baseline, mPaint);//调用drawText，居中显示绘制index
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(mPressedBackground);//手指按下时背景变色
                //注意这里没有break，因为down时，也要计算落点 回调监听器
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                //通过计算判断落点在哪个区域：
                int pressI = (int) ((y - getPaddingTop()) / mGapHeight);
                //边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI >= mIndexDatas.size()) {
                    pressI = mIndexDatas.size() - 1;
                }
                //回调监听器
                if (null != mOnIndexPressedListener && mIndexDatas.size() > 0) {
                    mOnIndexPressedListener.onIndexPressed(pressI, mIndexDatas.get(pressI));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                setBackgroundResource(android.R.color.transparent);//手指抬起时背景恢复透明
                //回调监听器
                if (null != mOnIndexPressedListener) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (null == mIndexDatas || mIndexDatas.isEmpty()) {
            return;
        }

        mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexDatas.size();
    }


    /**
     * 当前被按下的index的监听器
     */
    public interface onIndexPressedListener {
        void onIndexPressed(int index, String text);//当某个Index被按下

        void onMotionEventEnd();//当触摸事件结束（UP CANCEL）
    }

    private onIndexPressedListener mOnIndexPressedListener;

    public onIndexPressedListener getmOnIndexPressedListener() {
        return mOnIndexPressedListener;
    }

    public void setmOnIndexPressedListener(onIndexPressedListener mOnIndexPressedListener) {
        this.mOnIndexPressedListener = mOnIndexPressedListener;
    }

    /**
     * 显示当前被按下的index的TextView
     *
     * @return
     */

    public IndexBar setmPressedShowTextView(TextView mPressedShowTextView) {
        this.mPressedShowTextView = mPressedShowTextView;
        return this;
    }

    public IndexBar setmLayoutManager(LinearLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
        return this;
    }

    /**
     * 一定要在设置数据源{@link #setmSourceDatas(List)}之前调用
     *
     * @param needRealIndex
     * @return
     */
    public IndexBar setNeedRealIndex(boolean needRealIndex) {
        isNeedRealIndex = needRealIndex;
        if (isNeedRealIndex) {
            if (mIndexDatas != null) {
                mIndexDatas = new ArrayList<>();
            }
        }
        return this;
    }

    public IndexBar setmSourceDatas(List<String> mSourceDatas) {
        this.mSourceDatas = mSourceDatas;
        initSourceDatas();//对数据源进行初始化
        return this;
    }


    /**
     * 初始化原始数据源，并取出索引数据源
     *
     * @return
     */
    private void initSourceDatas() {
        if (null != mSourceDatas && mSourceDatas.size() == 0 && isNeedRealIndex) {
            mIndexDatas.clear();
        }

        if (null == mSourceDatas || mSourceDatas.isEmpty()) {
            return;
        }
        if (isNeedRealIndex) {
            mIndexDatas.clear();
        }

        int size = mSourceDatas.size();
        for (int i = 0; i < size; i++) {
            if (Character.isLetter(mSourceDatas.get(i).charAt(0))) {//如果是A-Z字母开头
                if (isNeedRealIndex) {//如果需要真实的索引数据源
                    if (!mIndexDatas.contains(mSourceDatas.get(i))) {//则判断是否已经将这个索引添加进去，若没有则添加
                        mIndexDatas.add(mSourceDatas.get(i));
                    }
                }
            } else {//特殊字母这里统一用#处理
                if (isNeedRealIndex) {//如果需要真实的索引数据源
                    if (!mIndexDatas.contains("#")) {
                        mIndexDatas.add("#");
                    }
                }
            }
        }
        sortData();
    }

    /**
     * 对数据源排序
     */
    private void sortData() {
        //对右侧栏进行排序 将 # 丢在最后
        Collections.sort(mIndexDatas, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.equals("#")) {
                    return 1;
                } else if (rhs.equals("#")) {
                    return -1;
                } else {
                    return lhs.compareTo(rhs);
                }
            }
        });
    }


    /**
     * 根据传入的pos返回tag
     *
     * @param tag
     * @return
     */
    private int getPosByTag(String tag) {
        if (null == mSourceDatas || mSourceDatas.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0; i < mSourceDatas.size(); i++) {
            if (tag.equals(mSourceDatas.get(i))) {
                return i;
            }
        }
        return -1;
    }


}
