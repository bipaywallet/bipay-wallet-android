package com.spark.bipaywallet.activity.start;

import com.spark.bipaywallet.R;
import com.spark.bipaywallet.activity.home.MainActivity;
import com.spark.bipaywallet.base.BaseActivity;
import com.spark.bipaywallet.instance.SharedPreferenceInstance;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动页
 */
public class StartActivity extends BaseActivity {
    private Timer timer;
    int n = 2;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_start;
    }


    @Override
    protected void initData() {
        super.initData();
        timerStart();
    }

    private void timerStart() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (n == 0) {
                    timer.cancel();
                    timer = null;
                    if (SharedPreferenceInstance.getInstance().getIsFirstUse()) {
                        showActivity(LeadActivity.class, null);
                    } else {
                        showActivity(MainActivity.class, null);
                    }
                    finish();
                }
                n--;
            }
        }, 10, 999);
    }


    @Override
    public void onBackPressed() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onBackPressed();
    }

}
