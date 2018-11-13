package com.spark.bipaywallet.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.spark.bipaywallet.utils.StringUtils;

/**
 * Created by Administrator on 2018/9/17 0017.
 */

public class WordAlignTextView extends android.support.v7.widget.AppCompatTextView {
    private float textSize;
    private float textLineHeight;
    //顶部
    private int top;
    //y轴
    private int y;
    //线
    private int lines;
    //底部
    private int bottom;
    //右边
    private int right;
    //左边
    private int left;
    //线字
    private int lineDrawWords;
    private char[] textCharArray;
    private float singleWordWidth;
    //每个字符的空隙
    private float lineSpacingExtra;

    private boolean isFirst = true;

    public WordAlignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                initTextInfo();
                return true;
            }
        });
    }

    public WordAlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordAlignTextView(Context context) {
        this(context, null, 0);
    }


    public void initTextInfo() {
        textSize = getTextSize();
        //获取线的高度
        textLineHeight = getLineHeight();
        left = 0;
        right = getRight();
        y = getTop();
        // 要画的宽度
        int drawTotalWidth = right - left;
        String text = getText().toString();
        if (!StringUtils.isEmpty(text) && isFirst) {
            textCharArray = text.toCharArray();
            TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.density = getResources().getDisplayMetrics().density;
            mTextPaint.setTextSize(textSize);
            // 获取单个单词的的宽度
            singleWordWidth = mTextPaint.measureText("一") + lineSpacingExtra;
            // 每行可以放多少个字符
            lineDrawWords = (int) (drawTotalWidth / singleWordWidth);
            int length = textCharArray.length;
            lines = length / lineDrawWords;
            if ((length % lineDrawWords) > 0) {
                lines = lines + 1;
            }
            isFirst = false;
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            int totalHeight = (int) (lines * textLineHeight + textLineHeight * 2 + getPaddingBottom() + getPaddingTop() + layoutParams.bottomMargin + layoutParams.topMargin);
            setHeight(totalHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bottom = getBottom();
        int drawTotalLine = lines;

        if (maxLine != 0 && drawTotalLine > maxLine) {
            drawTotalLine = maxLine;
        }

        for (int i = 0; i < drawTotalLine; i++) {
            try {
                int length = textCharArray.length;
                int mLeft = left;
                // 第i+1行开始的字符index
                int startIndex = (i * 1) * lineDrawWords;
                // 第i+1行结束的字符index
                int endTextIndex = startIndex + lineDrawWords;
                if (endTextIndex > length) {
                    endTextIndex = length;
                    y += textLineHeight;
                } else {
                    y += textLineHeight;
                }
                for (; startIndex < endTextIndex; startIndex++) {
                    char c = textCharArray[startIndex];
//         if (c == ' ') {
//           c = '\u3000';
//         } else if (c < '\177') {
//           c = (char) (c + 65248);
//         }
                    canvas.drawText(String.valueOf(c), mLeft, y, getPaint());
                    mLeft += singleWordWidth;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int maxLine;

    public void setMaxLines(int max) {
        this.maxLine = max;
    }

    public void setLineSpacingExtra(int lineSpacingExtra) {
        this.lineSpacingExtra = lineSpacingExtra;
    }

    /**
     * 判断是否为中文
     *
     * @return
     */
    public static boolean containChinese(String string) {
        boolean flag = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if ((c >= 0x4e00) && (c <= 0x9FA5)) {
                flag = true;
            }
        }
        return flag;
    }

    public static String ToDBC(String input) {
        // 导致TextView异常换行的原因：安卓默认数字、字母不能为第一行以后每行的开头字符，因为数字、字母为半角字符
        // 所以我们只需要将半角字符转换为全角字符即可
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
}
