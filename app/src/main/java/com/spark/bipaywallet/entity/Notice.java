package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class Notice implements Serializable {
//    "id": 104,
//            "merchantCode": "20180829144210448",
//            "merchantName": "测试商户",
//            "flag": 0,
//            "title": "公告",
//            "content": "<h3>www.btxjys.com</h3>\n<p>打造世界一流数字资产交易平台</p>\n<p>Copyright&copy;2018 BTX All Rights Reserved</p>\n",
//            "createTime": "2018-08-29 14:45:36",
//            "updateTime": "2018-08-29 14:45:36"

    private int id;
    private String merchantCode;
    private String merchantName;
    private int flag;
    private String title;
    private String content;
    private String createTime;
    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
