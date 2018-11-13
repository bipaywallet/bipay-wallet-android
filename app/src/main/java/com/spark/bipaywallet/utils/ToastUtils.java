package com.spark.bipaywallet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spark.bipaywallet.app.MyApplication;

import org.json.JSONObject;


/**
 * daiyy
 */
public class ToastUtils {
    private static Context context = MyApplication.getApp();
    private static boolean isShow = true;// 默认显示
    private static Toast mToast = null;//全局唯一的Toast

    /**
     * 获取string.xml 资源文件字符串
     * @param id 资源文件id
     * @return 资源文件对应的字符串
     */
    public static String getString(int id){
        return context.getResources().getString(id);
    }


    /**
     * 全局控制是否显示Toast
     *
     * @param isShowToast
     */
    public static void controlShow(boolean isShowToast) {
        isShow = isShowToast;
    }

    /**
     * 取消Toast显示
     */
    public void cancelToast() {
        if (isShow && mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showToast(CharSequence message) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showToast(int resId) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), resId, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, Toast.LENGTH_LONG);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showLong(int resId) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), resId, Toast.LENGTH_LONG);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration 单位:毫秒
     */
    public static void show(CharSequence message, int duration) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, duration);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param resId    资源ID:getResources().getString(R.string.xxxxxx);
     * @param duration 单位:毫秒
     */
    public static void show(int resId, int duration) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), resId, duration);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    /**
     * 自定义Toast的View
     *
     * @param message
     * @param duration 单位:毫秒
     * @param view     显示自己的View
     */
    public static void customToastView(CharSequence message, int duration, View view) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, duration);
            } else {
                mToast.setText(message);
            }
            if (view != null) {
                mToast.setView(view);
            }
            mToast.show();
        }
    }

    /**
     * 自定义Toast的位置
     *
     * @param message
     * @param duration 单位:毫秒
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void customToastGravity(CharSequence message, int duration, int gravity, int xOffset, int yOffset) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, duration);
            } else {
                mToast.setText(message);
            }
            mToast.setGravity(gravity, xOffset, yOffset);
            mToast.show();
        }
    }

    /**
     * 自定义带图片和文字的Toast，最终的效果就是上面是图片，下面是文字
     *
     * @param message
     * @param iconResId 图片的资源id,如:R.drawable.icon
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showToastWithImageAndText(CharSequence message, int iconResId, int duration, int gravity, int xOffset, int yOffset) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, duration);
            } else {
                mToast.setText(message);
            }
            mToast.setGravity(gravity, xOffset, yOffset);
            LinearLayout toastView = (LinearLayout) mToast.getView();
            ImageView imageView = new ImageView(MyApplication.getApp());
            imageView.setImageResource(iconResId);
            toastView.addView(imageView, 0);
            mToast.show();
        }
    }

    /**
     * 自定义Toast,针对类型CharSequence
     *
     * @param message
     * @param duration
     * @param view
     * @param isGravity        true,表示后面的三个布局参数生效,false,表示不生效
     * @param gravity
     * @param xOffset
     * @param yOffset
     * @param isMargin         true,表示后面的两个参数生效，false,表示不生效
     * @param horizontalMargin
     * @param verticalMargin
     */
    public static void customToastAll(CharSequence message, int duration, View view, boolean isGravity, int gravity, int xOffset, int yOffset, boolean isMargin, float horizontalMargin, float verticalMargin) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), message, duration);
            } else {
                mToast.setText(message);
            }
            if (view != null) {
                mToast.setView(view);
            }
            if (isMargin) {
                mToast.setMargin(horizontalMargin, verticalMargin);
            }
            if (isGravity) {
                mToast.setGravity(gravity, xOffset, yOffset);
            }
            mToast.show();
        }
    }

    /**
     * 自定义Toast,针对类型resId
     *
     * @param resId
     * @param duration
     * @param view             :应该是一个布局，布局中包含了自己设置好的内容
     * @param isGravity        true,表示后面的三个布局参数生效,false,表示不生效
     * @param gravity
     * @param xOffset
     * @param yOffset
     * @param isMargin         true,表示后面的两个参数生效，false,表示不生效
     * @param horizontalMargin
     * @param verticalMargin
     */
    public static void customToastAll(int resId, int duration, View view, boolean isGravity, int gravity, int xOffset, int yOffset, boolean isMargin, float horizontalMargin, float verticalMargin) {
        if (isShow) {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getApp(), resId, duration);
            } else {
                mToast.setText(resId);
            }
            if (view != null) {
                mToast.setView(view);
            }
            if (isMargin) {
                mToast.setMargin(horizontalMargin, verticalMargin);
            }
            if (isGravity) {
                mToast.setGravity(gravity, xOffset, yOffset);
            }
            mToast.show();
        }
    }

    /**
     * 弹出错误信息
     *
     * @param activity
     * @param object
     */
    public static void showNetMessage(Activity activity, JSONObject object) {
        if (object == null)
            return;
        int code = object.optInt("code");
        if (code == 4000) {
            showCofirmDialog(activity);
        } else {
            ToastUtils.showToast(object.optString("message"));
        }
    }

    /**
     * 确认是否退出登录
     */
    private static void showCofirmDialog(final Activity activity) {
//        final MaterialDialog dialog = new MaterialDialog(activity);
//        dialog.title(MyApplication.getApp().getString(R.string.warm_prompt)).titleTextColor(activity.getResources().getColor(R.color.colorPrimary)).content(MyApplication.getApp().getString(R.string.login_tag)).setOnBtnClickL(
//                new OnBtnClickL() {
//                    @Override
//                    public void onBtnClick() {
//                        dialog.dismiss();
//                    }
//                },
//                new OnBtnClickL() {
//                    @Override
//                    public void onBtnClick() {
////                        MyApplication.getApp().deleteCurrentUser();
////                        Intent intent = new Intent(activity, LoginMainActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        activity.startActivity(intent);
////                        ActivityManage.finishAll();
//                        dialog.superDismiss();
//                    }
//                });
//        dialog.show();
    }

}