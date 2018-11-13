package com.spark.bipaywallet.data;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.app.GlobalConstant;
import com.spark.bipaywallet.entity.HttpCoinMessage;
import com.spark.bipaywallet.entity.HttpETHMessage;
import com.spark.bipaywallet.entity.HttpNew;
import com.spark.bipaywallet.entity.KDataMessage;
import com.spark.bipaywallet.entity.KNewOneData;
import com.spark.bipaywallet.entity.MyCurrencyData;
import com.spark.bipaywallet.entity.MyDVCRecord;
import com.spark.bipaywallet.entity.MyETHRecord;
import com.spark.bipaywallet.entity.MyTokenRecord;
import com.spark.bipaywallet.entity.MyUSDTRecord;
import com.spark.bipaywallet.entity.TokenContract;
import com.spark.bipaywallet.entity.TokenQuery;
import com.spark.bipaywallet.entity.VersionMessage;
import com.spark.bipaywallet.factory.UrlFactory;
import com.spark.bipaywallet.hmac.Hmac;
import com.spark.bipaywallet.utils.LogUtils;
import com.spark.bipaywallet.utils.ToastUtils;
import com.spark.bipaywallet.utils.okhttp.OkhttpUtils;
import com.spark.bipaywallet.utils.okhttp.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.spark.bipaywallet.app.GlobalConstant.JSON_ERROR;
import static com.spark.bipaywallet.app.GlobalConstant.OKHTTP_ERROR;
import static com.spark.bipaywallet.utils.okhttp.OkhttpUtils.post;

public class RemoteDataSource implements DataSource {
    private static RemoteDataSource INSTANCE;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    private RemoteDataSource() {

    }

    /**
     * 有参数的post请求
     *
     * @param url
     * @param params
     * @param dataCallback
     */
    @Override
    public void doStringPost(final String url, HashMap<String, String> params, final DataCallback dataCallback) {
        OkhttpUtils.post().url(url).addParams(params).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) { // 请求异常，服务器异常，解析异常
                e.printStackTrace();
                dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {
                e.printStackTrace();
                if (code >= 400 && code < 500) {
                    dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, null);
                } else if (code >= 500) {
                    dataCallback.onDataNotAvailable(GlobalConstant.SERVER_ERROR, null);
                } else {
                    dataCallback.onDataNotAvailable(GlobalConstant.JSON_ERROR, null);
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtils.i("urll===" + url + ",,,,onResponse" + response);
                dataCallback.onDataLoaded(response);
            }

        });
        if (params != null) {
            String strParams = "";
            for (String key : params.keySet()) {
                strParams = strParams + key + "=" + params.get(key) + "&";
            }
            LogUtils.i("传参==" + url + "?" + strParams);
        }
    }

    /**
     * 无参数的post请求
     *
     * @param url
     * @param dataCallback
     */
    @Override
    public void doStringPost(final String url, final DataCallback dataCallback) {
        LogUtils.i("请求链接==" + url);
        OkhttpUtils.post().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {
                e.printStackTrace();
                if (code >= 400 && code < 500) {
                    dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, null);
                } else if (code >= 500) {
                    dataCallback.onDataNotAvailable(GlobalConstant.SERVER_ERROR, null);
                } else {
                    dataCallback.onDataNotAvailable(GlobalConstant.JSON_ERROR, null);
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtils.i("urll===" + url + ",,,,onResponse" + response);
                dataCallback.onDataLoaded(response);
            }

        });
    }

    /**
     * 传递json
     *
     * @param url
     * @param json
     * @param dataCallback
     */
    @Override
    public void doStringPost(String url, String json, final DataCallback dataCallback) {
        LogUtils.i("请求链接==" + url + "?" + json);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json")
                , json);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("x-auth-token", "20180829144210448")
                .addHeader("Content-Type", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                LogUtils.i("返回数据===" + responseStr);
                dataCallback.onDataLoaded(responseStr);
            }
        });
    }

    /**
     * 币币兑换
     */
    @Override
    public void getExchangeCoin(String json, final DataCallback dataCallback) {
        String sign = new Hmac().hmacSHA512(json, ToastUtils.getString(R.string.exchange_secret));
        Log.i("sx", sign);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json")
                , json);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlFactory.getExchangeUrl())
                .post(requestBody)
                .addHeader("sign", sign)
                .addHeader("api-key", ToastUtils.getString(R.string.exchange_key))
                .addHeader("Content-Type", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("sx", e.getMessage() + "eeee");
                dataCallback.onDataNotAvailable(GlobalConstant.OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.i("sx", responseStr);
                dataCallback.onDataLoaded(responseStr);
            }
        });
    }

    /**
     * 获取DVC币种信息
     */
    @Override
    public void getCoinMessage(final HashMap<String, String> params, final CoinDataCallback dataCallback) {
        post().url(UrlFactory.getCoinMessage(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取DVC币种信息出错", "获取DVC币种信息出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取DVC币种信息回执：", "获取DVC币种信息回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<HttpCoinMessage> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HttpCoinMessage>>() {
                        }.getType());
//                        HttpCoinMessage obj = gson.fromJson(object.getJSONObject("data").toString(), HttpCoinMessage.class);
                        dataCallback.onDataLoaded(objs.get(0), params.get("coinName"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 获取ETH币种信息
     */
    @Override
    public void getETHMessage(final HashMap<String, String> params, final CoinDataCallback dataCallback) {
        post().url(UrlFactory.getCoinMessage(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取ETH币种信息出错", "获取ETH币种信息出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取ETH币种信息回执：", "获取ETH币种信息回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<HttpETHMessage> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HttpETHMessage>>() {
                        }.getType());
//                        HttpETHMessage obj = gson.fromJson(object.getJSONObject("data").toString(), HttpETHMessage.class);
                        dataCallback.onDataLoaded(objs.get(0), params.get("coinName"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 获取Token币种信息
     */
    @Override
    public void getTokenMessage(final HashMap<String, String> params, final CoinDataCallback dataCallback) {
        post().url(UrlFactory.getTokenCoinMessage(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .addParams("contractAddress", params.get("contractAddress"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取Token信息出错", "获取Token信息出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取Token信息回执：", "获取Token信息回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<HttpETHMessage> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HttpETHMessage>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs.get(0), params.get("contractAddress"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 获取Token合约
     */
    @Override
    public void getTokenContract(final HashMap<String, String> params, final CoinDataCallback dataCallback) {
        post().url(UrlFactory.getTokenContract(params.get("coinName").toLowerCase()))
//                .addParams("address", params.get("address"))
                .addParams("contractAddress", params.get("address"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取token合约出错", "获取token合约出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取token合约回执：", "获取token合约回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        TokenContract obj = gson.fromJson(object.getJSONObject("data").toString(), TokenContract.class);
                        dataCallback.onDataLoaded(obj, params.get("ethAddress"));
                    } else if (object.optInt("code") == -1) {
                        dataCallback.onDataNotAvailable(object.getInt("code"), params.get("address") + ":" + params.get("ethAddress"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 获取Token合约symbol
     */
    @Override
    public void getTokenQuery(final HashMap<String, String> params, final CoinDataCallback dataCallback) {
        post().url(UrlFactory.getTokenQuery(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取token合约symbol出错", "获取token合约symbol出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取token合约symbol回执：", "获取token合约symbol回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        TokenQuery obj = gson.fromJson(object.getJSONObject("data").toString(), TokenQuery.class);
                        dataCallback.onDataLoaded(obj, params.get("ethAddress"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * DVC转账
     */
    @Override
    public void transferPay(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTransferPayUrl(params.get("coinName").toLowerCase()))
                .addParams("signTxStr", params.get("signTxStr"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("转账出错", "转账出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("转账回执：", "转账回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        String obj = object.getString("data");
                        dataCallback.onDataLoaded(obj);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * ETH转账
     */
    @Override
    public void transferPayETH(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTransferPayUrl(params.get("coinName").toLowerCase()))
                .addParams("signTxStr", params.get("signTxStr"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("转账出错", "转账出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("转账回执：", "转账回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        dataCallback.onDataLoaded(object.getString("data"));
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 查询DVC交易记录
     */
    @Override
    public void transactionRecord(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTransactionRecordUrl(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .addParams("start", params.get("start"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("查询交易记录出错", "查询交易记录出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("查询交易记录回执：", "查询交易记录回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<MyDVCRecord> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<MyDVCRecord>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 查询USDT交易记录
     */
    @Override
    public void transactionUSDTRecord(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTransactionRecordUrl(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .addParams("start", params.get("start"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("查询交易记录出错", "查询交易记录出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("查询交易记录回执：", "查询交易记录回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<MyUSDTRecord> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<MyUSDTRecord>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 查询ETH交易记录
     */
    @Override
    public void transactionETHRecord(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTransactionRecordUrl(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .addParams("start", params.get("start"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("查询交易记录出错", "查询交易记录出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("查询交易记录回执：", "查询交易记录回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<MyETHRecord> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<MyETHRecord>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 查询Token交易记录
     */
    @Override
    public void transactionTokenRecord(final HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getTokenTransactionRecordUrl(params.get("coinName").toLowerCase()))
                .addParams("address", params.get("address"))
                .addParams("contractAddress", params.get("contractAddress"))
                .addParams("start", params.get("start"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("查询Token交易记录出错", "查询Token交易记录出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("查询Token交易记录回执：", "查询Token交易记录回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<MyTokenRecord> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<MyTokenRecord>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 获取手续费
     */
    @Override
    public void getServiceCharge(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getServiceCharge(params.get("coinName").toLowerCase()))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("获取币种信息出错", "获取币种信息出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("获取币种信息回执：", "获取币种信息回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        String obj = object.getString("data");
                        dataCallback.onDataLoaded(obj);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 快讯
     */
    @Override
    public void getKuaixunList(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getKuaixunUrl())
                .addParams(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("查询快讯出错", "查询快讯出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("查询快讯回执：", "查询快讯回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<HttpNew> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HttpNew>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 首页获取币种数据
     */
    @Override
    public void getCurrencyDataList(final DataCallback dataCallback) {
        post().url(UrlFactory.getCurrencyDataUrl())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("币种数据出错", "币种数据出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("币种数据回执：", "币种数据回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<MyCurrencyData> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<MyCurrencyData>>() {
                        }.getType());
                        dataCallback.onDataLoaded(objs);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    /**
     * 版本更新
     */
    @Override
    public void getVersionMessage(final DataCallback dataCallback) {
        post().url(UrlFactory.getVersionUrl())
                .addHeader("x-auth-token", "20180829144210448")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("版本更新出错", "版本更新出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("版本更新回执：", "版本更新回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("responseCode") == 200) {
                        VersionMessage obj = gson.fromJson(object.getJSONObject("result").toString(), VersionMessage.class);
                        dataCallback.onDataLoaded(obj);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("responseCode"), object.optString("responseMessage"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    @Override
    public void KData(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getKDataUrl())
                .addParams(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("历史K线数据出错", "历史K线数据出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("历史K线数据回执：", "历史K线数据回执：" + response.toString());
//                try {
//                    dataCallback.onDataLoaded(new JSONArray(response));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
//                }

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        KDataMessage obj = gson.fromJson(object.getJSONObject("data").toString(), KDataMessage.class);
                        dataCallback.onDataLoaded(obj);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }

    @Override
    public void getNewKOneData(HashMap<String, String> params, final DataCallback dataCallback) {
        post().url(UrlFactory.getNewKOneData())
                .addParams(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtils.logi("K线类型对应最新一条数据出错", "K线类型对应最新一条数据出错：" + e.getMessage());
                dataCallback.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onError(Request request, Exception e, int code) {

            }

            @Override
            public void onResponse(String response) {
                LogUtils.logi("K线类型对应最新一条数据回执：", "K线类型对应最新一条数据回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        KNewOneData obj = gson.fromJson(object.getJSONObject("data").toString(), KNewOneData.class);
                        dataCallback.onDataLoaded(obj);
                    } else {
                        dataCallback.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataCallback.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });
    }


}
