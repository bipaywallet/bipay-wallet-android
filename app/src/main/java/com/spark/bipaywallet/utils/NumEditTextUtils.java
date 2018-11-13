package com.spark.bipaywallet.utils;


import android.widget.EditText;

/**
 * 判断金额输入框工具类
 */
public class NumEditTextUtils {

    /**
     * 限制输入框规则
     */
    public static boolean isVaildText(EditText editText) {
        String charSequence = editText.getText().toString();
        // 如果"."在起始位置,则起始位置自动补0
        if (charSequence.equals(".")) {
            charSequence = "0" + charSequence;
            editText.setText(charSequence);
            editText.setSelection(2);
            return false;
        }

        // 如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (charSequence.startsWith("0") && charSequence.trim().length() > 1) {
            if (!charSequence.substring(1, 2).equals(".")) {
                editText.setText(charSequence.subSequence(0, 1));
                editText.setSelection(1);
                return false;
            }
        }

        // 如果"."后超过8位,则无法后续输入
        String[] strs = charSequence.split("\\.");
        if (strs.length > 1) {
            if (strs[1].length() > 8) {
                editText.setText(charSequence.subSequence(0, charSequence.length() - 1));
                editText.setSelection(charSequence.length() - 1);
                return false;
            }
        }

        return true;
    }

    /**
     * 防止两个输入框textWatcher相互影响死循环，给EditText赋值时remove掉textWatcher
     */
    public static boolean isVaildText(EditText editText, MyTextWatcher textWatcher) {
        String charSequence = editText.getText().toString();
        // 如果"."在起始位置,则起始位置自动补0
        if (charSequence.equals(".")) {
            charSequence = "0" + charSequence;
            editText.removeTextChangedListener(textWatcher);
            editText.setText(charSequence);
            editText.setSelection(2);
            editText.addTextChangedListener(textWatcher);
            return false;
        }

        // 如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (charSequence.startsWith("0") && charSequence.trim().length() > 1) {
            if (!charSequence.substring(1, 2).equals(".")) {
                editText.removeTextChangedListener(textWatcher);
                editText.setText(charSequence.subSequence(0, 1));
                editText.setSelection(1);
                editText.addTextChangedListener(textWatcher);
                return false;
            }
        }

        // 如果"."后超过8位,则无法后续输入
        String[] strs = charSequence.split("\\.");
        if (strs.length > 1) {
            if (strs[1].length() > 8) {
                editText.removeTextChangedListener(textWatcher);
                editText.setText(charSequence.subSequence(0, charSequence.length() - 1));
                editText.setSelection(charSequence.length() - 1);
                editText.addTextChangedListener(textWatcher);
                return false;
            }
        }

        return true;
    }


}
