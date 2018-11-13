#ifndef BIPAYDLL_H
#define BIPAYDLL_H

#if defined(WIN32) || defined(_MSC_VER)
#ifdef BIPAY_EXPORT
#define BIPAY_DLL extern "C" __declspec(dllexport)
#else
#define BIPAY_DLL extern "C" __declspec(dllimport)
#endif // BIPAY_EXPORT
#else
#define BIPAY_DLL
#endif // WIN32 || _MSC_VER

/**
 * @brief 由助记词生成种子
 * @entropy 一个16进制字符串,长度为128~256
 */
BIPAY_DLL char* GetSeed(const char* mnemonic);

/**
 * @brief 由熵生成对应语言的助记词,助记词以空格分割
 * @language 生成助记词的语言,分为以下几类
 *      \ 0:en
 *		\ 1:es
 *		\ 2:ja
 *      \ 3:it
 *		\ 4:fr
 *		\ 5:cs
 *      \ 6:ru
 *		\ 7:uk
 *		\ 8:zh_Hans
 *		\ 9:zh_Hant
 */
BIPAY_DLL char* GetMnemonic(int language);

/**
 * @brief 获取主私钥,全局就一个主私钥,其它币种的私钥可由该私钥派生
 */
BIPAY_DLL char* GetMasterKey(const char* seed);

/**
 * @brief 获取某个币种的主公钥,不同的币种公钥形式可能不同
 *
 */
BIPAY_DLL char* GetPublicKey(const char* privkey, int coin_type);

/**
 * @brief 获取某个币种主公钥的导出形式,以xpub开头格式化字符串
 */
BIPAY_DLL char* GetExPublicKey(const char* privkey, int coin_type);


/***
 * * @brief 以下接口实现BIP32协议中对应某个币种子私钥和子公钥的生成方式,分别有如下对应:
 * *   1. 母私钥->子私钥  GetSubPrivKey
 * *   2. 母私钥->子公钥  GetPrivSubPubKey
 * *   3. 父公钥->子公钥  GetPubSubPubKey
 * *  所有的子公钥以xpub格式导出,可通过GetPublicKey方法获取各币种不同的形式
 * *  所有的子密钥以xprv格式导出,可通过GetPrivKey方法获取不同币种的密钥形式
 */
/**
 * @brief 获取某个币种的子密钥,以xprv格式导出
 * @pprivkey 对应某个币种的母密钥,有GetCoinMasterKey生成
 * @index 对应子密钥的索引,若index=[2^31, 2^32),则为强化子密钥否则为普通子密钥
 * @
 */
BIPAY_DLL char* GetSubPrivKey(const char* pprivkey, unsigned int index);

/**
 * @brief 根据母私钥获取子公钥,以xpub格式导出
 * @pprivkey 母私钥
 * @index 对应子密钥的索引,index=[0, 2^31)
 */
BIPAY_DLL char* GetPrivSubPubKey(const char* pprivkey, unsigned int index);

/**
 * @brief 根据父公钥获取子公钥,以xpub格式导出
 * @ppubkey 父公钥
 * @index 对应子公钥的索引,index=[0, 2^31)
 */
BIPAY_DLL char* GetPubSubPubKey(const char* ppubkey, unsigned int index);

/// 签名
BIPAY_DLL char* Signature(const char* tx, const char* privkeys, int coin_type);

BIPAY_DLL char* SignSignature(const char* tx, const char* privkeys, int coin_type, const char* reserved);

/// 通过keystore文件路径和密码对tx进行签名
BIPAY_DLL char* SignatureByKS(const char* tx, const char* ks_file, const char* pass, int coin_type);

///交易生成
/**
 * @brief 通过给定的json格式的交易参数来创建一个交易,比如以太坊:
 *        {
 *			"nonce":5,
 *			"gasprice":4000000000,
 *			...
 *		  }
 *	@tx_json_str 形如上面的json字符串
 *	@return 十六进制交易字符串
 */
BIPAY_DLL char* NewTransaction(const char* tx_json_str, int coin_type);

/**
 * @brief 用于验证某个币种的地址是否合法
*/
BIPAY_DLL bool VerifyAddress(const char* addr, int coin_type);

///在调用任意一个接口函数时,需要使用该接口来释放空间
BIPAY_DLL void FreeAlloc(char* oc);

/**
 * @brief 获取对应币种的主私钥
 *
 */
BIPAY_DLL char* GetCoinMasterKey(const char* master_key, int coin_type);

/**
* @brief 通过私钥获取导出形式的私钥
* @privkey base58形式的私钥
* @coin_type 币种类型
* @prefix 地址前缀
*/
BIPAY_DLL char* GetExportedPrivKey(const char* privkey, int coin_type, int prefix);

/**
 * @brief 通过私钥获取地址
 * @privkey base58形式的私钥
 * @coin_type 币种类型
 * @prefix 地址前缀
 */
BIPAY_DLL char* GetAddressUsePrivkey(const char* privkey, int coin_type, int prefix);

BIPAY_DLL char* GetAddressUsePublicKey(const char* pubkey, int coin_type, int prefix);

BIPAY_DLL char* GetSupportedCoins();

#endif
