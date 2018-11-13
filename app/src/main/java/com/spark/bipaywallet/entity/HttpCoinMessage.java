package com.spark.bipaywallet.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口获取的单个币种信息
 */
public class HttpCoinMessage implements Serializable {

//    {
//        "code":0,
//            "message":"success",
//            "data":{
//        "address":"184wTUutRt5geVhpCzhnp8vmKJWjUQGeah",
//                "totalAmount":50,
//                "utxo": [
//        {
//            "id":"MWI8-WQBGNhKejA8T2jk",
//                "txid":"aa94ee85a999c699a2972ab9c225ed108ded4253cba3dc05e246ad776ef261ee",
//                "indexNo":0,
//                "address":"184wTUutRt5geVhpCzhnp8vmKJWjUQGeah",
//                "amount":50,
//                "confirmations":534580,
//                "blockHeight":230,
//                "direction":"input",
//                "time":null,
//                "serviceCharge":0,
//                "transferAddress":null
//        }
//        ]
//    }
//    }

    private String address;
    private String totalAmount;
    private List<Utxo> utxo = new ArrayList<>(); // 未花交易结构体

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Utxo> getUtxo() {
        return utxo;
    }

    public void setUtxo(List<Utxo> utxo) {
        this.utxo = utxo;
    }

    public static class Utxo implements Serializable {
        private String id;
        private String txid; // 区块里中交易编号
        private String indexNo; // 这笔交易对应上次输出的序号
        private String address;
        private String amount;
        private String confirmations;
        private String blockHeight;
        private String direction;
        private String time;
        private String serviceCharge;
        private String transferAddress;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getIndexNo() {
            return indexNo;
        }

        public void setIndexNo(String indexNo) {
            this.indexNo = indexNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getConfirmations() {
            return confirmations;
        }

        public void setConfirmations(String confirmations) {
            this.confirmations = confirmations;
        }

        public String getBlockHeight() {
            return blockHeight;
        }

        public void setBlockHeight(String blockHeight) {
            this.blockHeight = blockHeight;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getServiceCharge() {
            return serviceCharge;
        }

        public void setServiceCharge(String serviceCharge) {
            this.serviceCharge = serviceCharge;
        }

        public String getTransferAddress() {
            return transferAddress;
        }

        public void setTransferAddress(String transferAddress) {
            this.transferAddress = transferAddress;
        }
    }

}
