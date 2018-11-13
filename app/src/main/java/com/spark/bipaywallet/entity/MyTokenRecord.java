package com.spark.bipaywallet.entity;


import java.io.Serializable;
import java.util.List;

public class MyTokenRecord implements Serializable {

//    "data": [
//    {
//        "address": "0x7aa1ff2abfa0e7e282f8a2f14105cd4e5d8def0a",
//            "normalTransactions": [
//        {
//            "contractAddress": "0xd1e4b75190349e1fc9dbc77c71999c0461407526",
//                "txid": "0x76396d015157bc09a38140476794ea06e721a880752fed366b9df471d2a118b7",
//                "blockHash": "0x94d0c0f2da1d34070e8a0b3fd8e84849140b621da161217b425aa99dc47db287",
//                "blockHeight": 6372843,
//                "time": "2018-09-21 21:58:04",
//                "amount": 0,
//                "confirmations": 21467,
//                "from": "0xfe735c3d177876d8a3881f17ce82ea41b7cfee17",
//                "to": "0x7aa1ff2abfa0e7e282f8a2f14105cd4e5d8def0a",
//                "value": 0,
//                "fee": 0.0009,
//                "direction": null
//        }
//            ]
//    }
//    ]

    private String address;
    private List<TokenNormalTransaction> normalTransactions;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<TokenNormalTransaction> getNormalTransactions() {
        return normalTransactions;
    }

    public void setNormalTransactions(List<TokenNormalTransaction> normalTransactions) {
        this.normalTransactions = normalTransactions;
    }

    public static class TokenNormalTransaction implements Serializable {
        private String contractAddress;
        private String txid;
        private String blockHash;
        private String blockHeight;
        private String time;
        private String amount;
        private String confirmations;
        private String from;
        private String to;
        private String value;
        private String fee;
        private String direction;

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getBlockHeight() {
            return blockHeight;
        }

        public void setBlockHeight(String blockHeight) {
            this.blockHeight = blockHeight;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }


}
