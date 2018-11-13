package com.spark.bipaywallet.config;

import android.content.Context;

import com.spark.bipaywallet.data.DataRepository;
import com.spark.bipaywallet.data.LocalDataSource;
import com.spark.bipaywallet.data.RemoteDataSource;

/**
 * Created by Administrator on 2017/9/25.
 */

public class Injection {
    public static DataRepository provideTasksRepository(Context context) {
        return DataRepository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(context));
    }
}
