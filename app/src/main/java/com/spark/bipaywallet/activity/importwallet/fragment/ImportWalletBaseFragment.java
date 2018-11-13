package com.spark.bipaywallet.activity.importwallet.fragment;

import android.content.Context;

import com.spark.bipaywallet.base.BaseFragment;

/**
 * Created by Administrator on 2018/2/26.
 */

public abstract class ImportWalletBaseFragment extends BaseFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof ImportWalletCallback)) {
            throw new RuntimeException("The Activity which this fragment is located must implement the ImportWalletCallback interface!");
        }
    }


    public interface ImportWalletCallback {
        void success();
    }

}
