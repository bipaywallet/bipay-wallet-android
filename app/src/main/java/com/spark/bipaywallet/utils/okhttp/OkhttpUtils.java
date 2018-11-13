package com.spark.bipaywallet.utils.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.StringUtils;
import com.spark.bipaywallet.utils.okhttp.get.GetBuilder;
import com.spark.bipaywallet.utils.okhttp.post.PostFormBuilder;
import com.spark.bipaywallet.utils.okhttp.post.PostJsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkhttpUtils {
    private static OkhttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler handler;

    public OkhttpUtils() {
//        mOkHttpClient = new OkHttpClient();
        mOkHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                cookieStore.put(httpUrl.host(), list);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkhttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkhttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpUtils();
                }
            }
        }
        return mInstance;
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostJsonBuilder postJson() {
        return new PostJsonBuilder();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void execute(RequestCall requestCall, Callback callback) {
        if (callback == null) callback = Callback.CALLBACK_DEFAULT;
        LogUtils.i("URL" + requestCall.getCall().request().url().toString());
        final Callback finalCallback = callback;
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailResultCallback(call.request(), e, finalCallback);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(call.request(), new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    String token = response.header("x-auth-token");
                    if (!StringUtils.isEmpty(token)) {
//                        MyApplication.getApp().getCurrentUser().setToken(token);
//                        MyApplication.getApp().saveCurrentUser();
                    }
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (IOException e) {
                    sendFailResultCallback(response.request(), e, finalCallback);
                }
            }
        });
    }

    public void sendFailResultCallback(final Request request, final Exception e, final Callback callback) {
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void sendFailResultCallback(final Request request, final Exception e, final Callback callback, final int code) {
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e, code);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public Handler getHandler() {
        return handler;
    }
}
