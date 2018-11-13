package com.spark.bipaywallet.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 计算工具类
 */
public class MathUtils {
    //精确到n位小数
    public static String getBigDecimalRundNumber(String number, int n) {
        DecimalFormat df;
        if (n == 2) {
            df = new DecimalFormat("0.00");
        } else if (n == 0) {
            df = new DecimalFormat("0");
        } else {
            df = new DecimalFormat("0.00000000");
        }
        if (StringUtils.isNotEmpty(number)) {
            BigDecimal resultBD = new BigDecimal(number.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            return df.format(resultBD);
        } else {
            return df.format(0);
        }
    }

    //BigDecimal加
    public static String getBigDecimalAdd(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.add(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toString();
        } else {
            return "0";
        }
    }

    //BigDecimal减
    public static String getBigDecimalSubtract(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.subtract(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toString();
        } else {
            return "0";
        }
    }

    //BigDecimal乘
    public static String getBigDecimalMultiply(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.multiply(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toString();
        } else {
            return "0";
        }
    }

    //BigDecimal除
    public static String getBigDecimalDivide(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.divide(bBD, n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toString();
        } else {
            return "0";
        }
    }

    //BigDecimal乘方
    public static String getBigDecimalPow(String number1, int number2, int n) {
        if (StringUtils.isNotEmpty(number1)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.pow(number2).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toString();
        } else {
            return "0";
        }
    }

    //BigDecimal 10的8次方
    public static String getBigDecimal10Pow8() {
        BigDecimal aBD = new BigDecimal("10").setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultBD = aBD.pow(8).setScale(8, BigDecimal.ROUND_HALF_UP);
        return resultBD.toString();
    }

    //BigDecimal 10的18次方
    public static String getBigDecimal10Pow18() {
        BigDecimal aBD = new BigDecimal("10").setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultBD = aBD.pow(18).setScale(8, BigDecimal.ROUND_HALF_UP);
        return resultBD.toString();
    }

    //BigDecimal对比 BigDecimal为小于val返回-1，如果BigDecimal为大于val返回1，如果BigDecimal为等于val返回0
    public static int getBigDecimalCompareTo(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            return aBD.compareTo(bBD);
        } else {
            return -2;
        }
    }


}
