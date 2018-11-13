package com.spark.bipaywallet.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 本地保存的币种
 */
public class MyCoin extends DataSupport implements Serializable {

    private String walletName;//对应钱包名称
    private String name;//币种名称
    private String subPrivKey;//可生成地址和签名私钥的私钥
    private String address;//地址
    private String num;//币种资金余额
    private int coinType;//币种对应的数字
    private boolean isAdded;//用户是否添加，数据库中以0(false)和1(true)来存储
    private String rate;//对人民币的汇率
    private String usdRate;//对美元的汇率
    private String logoUrl;
    private long blockHeight;
    private String contractAddress;//合约地址
    private int decimals;

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubPrivKey() {
        return subPrivKey;
    }

    public void setSubPrivKey(String subPrivKey) {
        this.subPrivKey = subPrivKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUsdRate() {
        return usdRate;
    }

    public void setUsdRate(String usdRate) {
        this.usdRate = usdRate;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

}
