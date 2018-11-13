package com.spark.bipaywallet.utils.okhttp;


import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/29.
 */

public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response) throws IOException {
        return response.body().string();
    }
}
