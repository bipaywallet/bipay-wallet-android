## bipaysdk使用和币种说明
#### 1.下载SDK
> [SDK1.0.0 下载地址](https://bitrade.oss-cn-hongkong.aliyuncs.com/bifu_sdk/bipaysdk.rar)

#### 2.将SDK 导入项目
##### 导入动态库
>配置NDK路径，将提供的sdk以module方式导入到AndroidStudio中，主工程添加依赖，创建JNIUtil对象，即可调用相关方法。

#### 3.币种说明

币种名称|cointype|地址前缀|私钥前缀
-|-|-|-
BTC|0|128|0
BCH|145|128|0
ETH|60|-1|-1
LTC|2|176|48
USDT|207|128|0
XNE|208|176|75
DVC|206|176|30

#### 4.API说明（API Specification）
<font face="Times New Roman" size="5" color="#388dee">  API - getSupportedCoins</font><br />
```
/**
 获取支持的币种

 @return 支持币种字符串
 */
 public native String getSupportedCoins();
 
```

 **功能说明**

-  获取支持的币种

**参数说明**
          
-  无参数
***

<font face="Times New Roman" size="5" color="#388dee"> API - getMnemonic</font><br />
```
/**
 获取助记词，由熵生成对应语言的助记词,助记词以空格分割

 @param language 生成助记词的语言,分为: 0:en 1:es  2:ja 3:it  4:fr 5:cs  6:ru  7:uk  8:zh_Hans 9:zh_Hant

 @return 获取助记词
 */
public native String getMnemonic(int language);

```

 **功能说明**

-   获取助记词

 **参数说明**

-   language：0 - 9  暂时只支持 8-中文，0-英文
*** 

<font face="Times New Roman" size="5" color="#388dee">  API - getSeedWithMnemonic</font><br />
```
 
/**
 由助记词生成种子

 @param mnemonic 一个16进制字符串,长度为128~256 由getMnemonic获得
 @return 需要的种子
 */
public native String getSeedWithMnemonic(String mnemonic);

```

 **功能说明**

-   获取种子

 **参数说明**

-   mnemonic:助记词，由getMnemonic获得
***

<font face="Times New Roman" size="5" color="#388dee">  API - getMasterKey</font><br />
```
/**
 * 由种子获取主私钥,全局就一个主私钥,其它币种的私钥可由该私钥派生
 * @param seed 种子 由getSeedWithMnemonic获得
 * @return 私钥
 */
public native String getMasterKey(String seed);

```

 **功能说明**

-   获取主私钥

 **参数说明**

-   seed: 种子，由getSeedWithMnemonic获得
 ***

<font face="Times New Roman" size="5" color="#388dee">  API - getExPrivKey</font><br />
```
 
/**
 根据主私钥获取对应币种的母密钥（获取私钥以xprv序列化格式，用户不可见）

 @param masterKey 主私钥，由getMasterKey获得
 @param coinType 币种，对应值参考币种说明
 @return 私钥
 */
public native String getExPrivKey(String masterKey, int coinType);

```

 **功能说明**

-   根据主私钥获取对应币种的母密钥（获取私钥以xprv序列化格式，用户不可见）

 **参数说明**

-   masterKey: 主私钥，由getMasterKey获得
-   coinType:  币种，对应值参考币种说明
***

<font face="Times New Roman" size="5" color="#388dee"> API - getSubPrivKey</font><br />
```
 
/**
 获取某个币种的子密钥,以xprv格式导出,可生成币种地址和签名

 @param exPrivKey 对应某个币种的母密钥,由getExPrivKey生成
 @param index 对应子密钥的索引,若index=[2^31,2^32),则为强化子密钥否则为普通子密钥
 @return 获取某个币种的子密钥,以xprv格式导出
 */
public native String getSubPrivKey(String exPrivKey, long index);

```

 **功能说明**

-   获取某个币种的子密钥,以xprv格式导出,可生成币种地址和签名

 **参数说明**

-   exPrivKey: 对应某个币种的母密钥,有getExPrivKey生成
-   index:对应子密钥的索引，值为2 ^ 31

 ***
<font face ="Times New Roman" size = "5" color="#388dee"> API - getCoinAddress</font>
```

/**
 根据私钥获取地址 币种对应的地址

 @param subPrivKey 币种的子密钥，由getSubPrivKey生成
 @param coinType 币种，对应值参考币种说明
 @param prefix 地址前缀，比特币系列增加整型的prefix参数，用于衍生代币的地址前缀，其它币暂时可任意值(默认可使用－1)
 @return 地址
 */
public native String getCoinAddress(String subPrivKey, int coinType, int prefix);

```

 **功能说明**

-   币种对应的地址

 **参数说明**

-   subPrivKey:币种的子密钥，由getSubPrivKey生成
-   coinType:币种，对应值参考币种说明

***

<font face="Times New Roman" size="5" color="#388dee"> API - getSignaturePrivKey</font><br />
```

 /**
   币种地址签名的私钥,可导出的币种私钥，用户可见

 @param subPrivKey 币种的子密钥，由getSubPrivKey生成
 @param coinType 币种，对应值参考币种说明
 @param prefix 私钥前缀，比特币系列增加整型的prefix参数，用于衍生代币导出前缀，其它币暂时可任意值(默认可使用－1)
 @return 密钥
 */
public native String getSignaturePrivKey(String subPrivKey, int coinType, int prefix);

```
 **功能说明**

-   币种地址签名的私钥,可导出的币种私钥，用户可见

 **参数说明**

-   subPrivKey:币种的子密钥，由getSubPrivKey生成
-   coinType:币种，币种，对应值参考币种说明
***

<font face="Times New Roman" size="5" color="#388dee"> API - freeAlloc</font><br />
```
 
/**
 在调用任意一个接口函数时,需要使用该接口来释放空间

 @param oc 所有String类型的返回值
 */
public native String freeAlloc(String oc);

```

  **功能说明**

-   在调用任意一个接口函数时,需要使用该接口来释放空间

 **参数说明**

-   oc:所有String类型的返回值
***

<font face="Times New Roman" size="5" color="#388dee"> API - verifyCoinAddress</font><br />
```
 
   /**
 *  用于验证某个币种的地址是否合法
 
 @param addr 币种地址
 @param coinType 币种，对应值参考币种说明
 @return 是否合法
 */
public native boolean verifyCoinAddress(String addr, int coinType);

```
 **功能说明**

-   用于验证某个币种的地址是否合法

 **参数说明**

-   addr:币种地址
-   coinType:币种，对应值参考币种说明

***
<font face="Times New Roman" size="5" color="#388dee"> API - createNewTransaction</font><br />
```
 
/**
 获取signature的第一个参数tx
 通过给定的json格式的交易参数来创建一个交易,比如以太坊:
 *        {
 *            "nonce":5,
 *            "gasprice":4000000000,
 *            ...
 *          }
 @param json 形如上面的json字符串,根据币种生成的json
 @param coinType 币种，对应值参考币种说明
 @return 交易字符串tx
 */
public native String createNewTransaction(String json, int coinType);

```
 **功能说明**

-   获取signature:的第一个参数tx

 **参数说明**

-   json: 根据币种生成的json
-   coinType:币种，对应值参考币种说明

 **json说明**

```
ETH
    {
    "from":"0x389b5c399d998a71f816449ab31c9d2c358c082f",  // 转账地址
    "to":"0xc52f519474f6f0def379c9d161b0a91ec47582f2", // 转入地址
    "value":"10000000000000", // 转账金额
    "nonce":"2", // 交易次数
    "gasprice":"7000000000" // 手续费
     }    
BTC、DVC、LTC、XNE、BCH
{
    "inputs_count": 3, // 需要用到的交易结构体个数
    "inputs": [
        {
            "prev_position": 0, // 上次输出的序号
            "prev_tx_hash": "" // 交易编号
        },
        {
            "prev_position": 0,
            "prev_tx_hash": ""
        }
    ],
    "outputs_count": 2, // 不需要找零时为1，需要找零时为2
    "outputs": [
        {
            "address": "", // 对方的接收地址
            "value": "" // 转账金额
        },
        {
            "address": "", // 自己的转账地址（需要找零时，追加此字段和value字段）
            "value": "" // 找零金额
        }
    ]
}
USDT
{
    "inputs_count": 3, // 需要用到的交易结构体个数
    "inputs": [
        {
            "prev_position": 0, // 上次输出的序号
            "prev_tx_hash": "" // 交易编号
        },
        {
            "prev_position": 0,
            "prev_tx_hash": ""
        }
    ],
    "outputs_count": 2, // 不需要找零时为1，需要找零时为2
    "outputs": [
        {
            "address": "", // 对方的接收地址
            "value": "" // 转账金额
        },
        {
            "address": "", // 自己的转账地址（需要找零时，追加此字段和value字段）
            "value": "" // 找零金额
        }
    ],
    "usdt":[
        31,
        计算得到的数（实际转账金额 * 10的8次方）
    ]

}
```
 ***

<font face="Times New Roman" size="5" color="#388dee">API - signatureForTransfer（默认转账时使用）</font><br />
```

/**
 签名,生成转账参数

 @param tx  createNewTransaction方法返回的数据
 @param privkeys 由getSignaturePrivKey方法获得，createNewTransaction的json中有几个交易结构体，就追加几个privkeys，用空格" "分开
 @param coinType 币种，对应值参考币种说明
 @return 签名后字符串,为了发送交易给后台
 */
public native String signatureForTransfer(String tx, String privkeys, int coinType);

```

 **参数说明**

-   tx:由createNewTransaction方法返回的数据
-   privkeys:由getSignaturePrivKey方法获得，createNewTransaction的jso中有几个交易结构体，就追加几个privkeys，用空格" "分开
-   coinType: 币种，对应值参考币种说明

<font face="Times New Roman" size="5" color="#388dee">API - signatureForTransfer（BCH币种转账时使用）</font><br />
```

/**
 签名,生成转账参数

 @param tx  createNewTransaction方法返回的数据
 @param privkeys 由getSignaturePrivKey方法获得，createNewTransaction的json中有几个交易结构体，就追加几个privkeys，用空格" "分开
 @param coinType 币种，对应值参考币种说明
 @param reserved 构建的交易json字符串
{
    amount:[
        需要用到的交易结构体对应的amount（有几个结构体就写几个）
    ]
}
 @return 签名后字符串,为了发送交易给后台
 */
public native String signatureForTransfer(String tx, String privkeys, int coinType, String reserved);

```

 **参数说明**

-   tx:由createNewTransaction方法返回的数据
-   privkeys:由getSignaturePrivKey方法获得，createNewTransaction的jso中有几个交易结构体，就追加几个privkeys，用空格" "分开
-   coinType: 币种，对应值参考币种说明
-   reserved: 构建的交易json字符串