package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class VersionMessage implements Serializable {

//"result": {
//        "id": "2",
//                "merchantCode": "20180829144210448",
//                "merchantName": "测试商户",
//                "name": "币付钱包App",
//                "packageType": null,
//                "android": {
//            "id": "11",
//                    "code": "v1.1",
//                    "name": "",
//                    "download": "222",
//                    "status": 1,
//                    "platform": 0,
//                    "appId": "2"
//        },
//        "ios": {
//            "id": "10",
//                    "code": "v1.0",
//                    "name": "",
//                    "download": "ddd",
//                    "status": 1,
//                    "platform": 1,
//                    "appId": "2"
//        },
//        "intro": "币付钱包App",
//                "createTime": 1536306378000,
//                "updateTime": 1536560044000
//    },

    private String id;
    private String merchantCode;
    private String merchantName;
    private String name;
    private String packageType;
    private AndroidMsg android;
    private AndroidMsg ios;
    private String intro;
    private String createTime;
    private String updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public AndroidMsg getAndroid() {
        return android;
    }

    public void setAndroid(AndroidMsg android) {
        this.android = android;
    }

    public AndroidMsg getIos() {
        return ios;
    }

    public void setIos(AndroidMsg ios) {
        this.ios = ios;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public static class AndroidMsg implements Serializable {
        private String id;
        private String code;
        private String name;
        private String download;
        private String status;
        private String platform;
        private String appId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }
    }

}
