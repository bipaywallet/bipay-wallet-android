package com.spark.bipaysdk.jni;


public class JNIUtil {
    static {
        System.loadLibrary("native-lib");
    }

    private static JNIUtil INSTANCE;

    public static JNIUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JNIUtil();
        }
        return INSTANCE;
    }

    /**
     * 获取支持的币种
     *
     * @return
     */
    public native String getSupportedCoins();

    /**
     * 获取助记词
     *
     * @param language 8-中文，0-英文
     * @return
     */
    public native String getMnemonic(int language);


    /**
     * 获取种子
     *
     * @param mnemonic
     * @return
     */
    public native String getSeedWithMnemonic(String mnemonic);

    /**
     * 获取主私钥
     *
     * @param seed 种子
     * @return
     */
    public native String getMasterKey(String seed);

    /**
     * 根据主私钥获取对应币种的私钥,获取私钥以xprv序列化格式
     *
     * @param masterKey 主私钥
     * @param coinType  币种，0-BTC;60-ETH;206-DVC;
     * @return
     */
    public native String getExPrivKey(String masterKey, int coinType);

    /**
     * 可生成地址和签名私钥的私钥
     *
     * @param exPrivKey 币种私钥
     * @param index     对应子密钥的索引，值为2 ^ 31
     * @return
     */
    public native String getSubPrivKey(String exPrivKey, long index);

    /**
     * 币种对应的地址
     *
     * @param subPrivKey 币种的子密钥，由GetSubPrivKey生成
     * @param coinType   币种，0-BTC;60-ETH;206-DVC;
     * @return
     */
    public native String getCoinAddress(String subPrivKey, int coinType, int prefix);

    /**
     * 币种地址签名的私钥
     *
     * @param subPrivKey 币种的子密钥，由GetSubPrivKey生成
     * @param coinType   币种，0-BTC;60-ETH;206-DVC;
     * @return
     */
    public native String getSignaturePrivKey(String subPrivKey, int coinType, int prefix);

    /**
     * 获取Signature（）的第一个参数
     *
     * @param json
     * @param coinType
     * @return
     */
    public native String createNewTransaction(String json, int coinType);

    /**
     * 生成转账参数
     *
     * @param tx
     * @param privkeys
     * @param coin_type
     * @return
     */
    public native String signatureForTransfer(String tx, String privkeys, int coin_type);

    /**
     * 生成转账参数
     *
     * @param tx
     * @param privkeys
     * @param coin_type
     * @return
     */
    public native String SignSignatureForTransfer(String tx, String privkeys, int coin_type, String reserved);

    /**
     * 在调用任意一个接口函数时,需要使用该接口来释放空间
     *
     * @param oc 所有String类型的返回值
     * @return
     */
    public native String freeAlloc(String oc);

    /**
     * 用于验证某个币种的地址是否合法
     *
     * @param addr     币种地址
     * @param coinType 币种
     * @return
     */
    public native boolean verifyCoinAddress(String addr, int coinType);

}
