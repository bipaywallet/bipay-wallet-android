package com.spark.bipaywallet.base;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTabFragmentActivity extends BaseActivity {
    protected List<BaseFragment> fragments = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) initFragments();
        super.onCreate(savedInstanceState);
    }

    protected abstract void recoverFragment();

    protected abstract void initFragments();
}
