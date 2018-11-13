package com.spark.bipaywallet.entity;


import java.io.Serializable;
import java.util.List;

public class MyETHRecord implements Serializable {

//"address": "0xa89ac93b23370472daac337e9afdf642543f3e57",
//        "normalTransactions": [
//    {
//        "txid": "0x0525cb55f61b71d94a539cbe0148287161d56dd8753350193ee95624f815c908",
//            "blockHash": "0x0fbe59c9e770c3904bab5663a014b1827b3a1676067d8a52908249d880720eaf",
//            "blockHeight": 46486,
//            "time": "2015-08-07 13:06:56",
//            "amount": 1000,
//            "confirmations": 6187136,
//            "from": "0xa89ac93b23370472daac337e9afdf642543f3e57",
//            "to": "0xb5046cb3dc1dedbd364514a2848e44c1de4ed147",
//            "value": 0,
//            "fee": 0.0105,
//            "direction": null
//    },
//    {
//        "txid": "0xcc0e26052aed89450d3500ac6d3bc2c4ffa91db26e81eaf85a1345b0c98bb0a7",
//            "blockHash": "0xd4202ffd00409cfb9a2b6637a623bdbecec1d21147d548daf5c559a0afc6e703",
//            "blockHeight": 46664,
//            "time": "2015-08-07 13:59:26",
//            "amount": 10,
//            "confirmations": 6186958,
//            "from": "0xa89ac93b23370472daac337e9afdf642543f3e57",
//            "to": "0x92c27672fe65e002159ec2597fcf8897adbf5b29",
//            "value": 0,
//            "fee": 0.003329252931249,
//            "direction": null
//    },
//    {
//        "txid": "0xb7c8df07b78561d9c5ed1b8444b6660cf0614b11f0980cc6fde7a030edd653e3",
//            "blockHash": "0xc2404230bb98d42eb534151d84bdfc97a6a5df48509d5d54860ddd232df43484",
//            "blockHeight": 46731,
//            "time": "2015-08-07 14:17:46",
//            "amount": 0.86,
//            "confirmations": 6186891,
//            "from": "0xa89ac93b23370472daac337e9afdf642543f3e57",
//            "to": "0x92c27672fe65e002159ec2597fcf8897adbf5b29",
//            "value": 0,
//            "fee": 0.001702162011165,
//            "direction": null
//    }
//            ]

    private String address;
    private List<ETHNormalTransaction> normalTransactions;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ETHNormalTransaction> getNormalTransactions() {
        return normalTransactions;
    }

    public void setNormalTransactions(List<ETHNormalTransaction> normalTransactions) {
        this.normalTransactions = normalTransactions;
    }

    public static class ETHNormalTransaction extends BaseNormalTransaction implements Serializable {
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
