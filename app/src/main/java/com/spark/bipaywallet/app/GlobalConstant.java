package com.spark.bipaywallet.app;

/**
 * Created by Administrator on 2017/5/5.
 */

public class GlobalConstant {
    public static final boolean isDebug = true; // 是否代开log日志

    //自定义错误码
    public static final int JSON_ERROR = -9999;
    //    public static final int VOLLEY_ERROR = -9998;
//    public static final int TOAST_MESSAGE = -9997;
    public static final int OKHTTP_ERROR = -9996;
    //    public static final int NO_DATA = -9995;
    public static final int SERVER_ERROR = -9994;
    //permission
    public static final int PERMISSION_CONTACT = 0;
    public static final int PERMISSION_CAMERA = 1;
    public static final int PERMISSION_STORAGE = 2;
    public static final int PERMISSION_INSTALL_PACKAGES = 3;

    //常用常量
    public static final int TAKE_PHOTO = 10;
    public static final int CHOOSE_ALBUM = 11;

    /**
     * k线图对应tag值
     */
    public static final int TAG_DIVIDE_TIME = 0; // 分时图
    public static final int TAG_ONE_MINUTE = 1; // 1分钟
    public static final int TAG_FIVE_MINUTE = 2; // 5分钟
    public static final int TAG_AN_HOUR = 3; // 1小时
    public static final int TAG_DAY = 4; // 1天
    public static final int TAG_THIRTY_MINUTE = 5; // 30分钟
    public static final int TAG_WEEK = 6; // 1周
    public static final int TAG_MONTH = 7; // 1月
    public static final int TAG_SYS = 1001; // 扫一扫
    public static final int TAG_LXR = 1002; // 联系人
    public static final int TAG_XXZX = 1003; // 消息中心
    public static final int TAG_HBSZ = 1004; // 货币设置
    public static final int TAG_XTSZ = 1005; // 系统设置
    public static final int TAG_BZZX = 1006; // 帮助中心
    public static final int TAG_GYWM = 1007; // 关于我们
    public static final int TAG_BBGX = 1008; // 版本更新
    public static final int TAG_QBGL = 1009; // 钱包管理
    public static final int TAG_YYSZ = 1010; // 语言设置
    public static final int TAG_SHARE = 1011; // 分享APP

}
