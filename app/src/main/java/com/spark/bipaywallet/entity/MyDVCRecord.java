package com.spark.bipaywallet.entity;


import java.io.Serializable;
import java.util.List;

public class MyDVCRecord implements Serializable {

//"address": "DEYeybizBF9NQC8fBenrCxbVJNSkvQ5RZD",
//        "normalTransactions": [
//    {
//        "txid": "58feed3b4bdf4469972ae8acab6c310244b056f70ba9b0603032d4b9ea3d328a",
//            "blockHash": "6d144dcffbbff08b86bfc83460cda39f0c0a2a46b91be62c53630de8b19a8ed6",
//            "blockHeight": 61742,
//            "time": "2018-08-13 13:43:48",
//            "amount": 100.010022,
//            "confirmations": 1476,
//            "inputs": [
//        {
//            "address": "DGUj9MdAWgDWX9o3mMXx44TSyRa8Rco67V",
//                "amount": 2.50067,
//                "txid": "c2745f052a240ff6f945552650abf5b1630f123a4e230c5a882e7c2cae8a4916",
//                "indexNo": 1
//        },
//        {
//            "address": "DGUj9MdAWgDWX9o3mMXx44TSyRa8Rco67V",
//                "amount": 2.503334,
//                "txid": "91b88b888303a4bbf3bbd18c8d7876f34f978fae7b26d40b16dd58e01ed49dec",
//                "indexNo": 1
//        },
//        {
//            "address": "DGUj9MdAWgDWX9o3mMXx44TSyRa8Rco67V",
//                "amount": 47.509918,
//                "txid": "759135e95beda6ff1fa84223d57b8551ae1050a885929b83dd65c55181d8d2ff",
//                "indexNo": 0
//        },
//        {
//            "address": "DGUj9MdAWgDWX9o3mMXx44TSyRa8Rco67V",
//                "amount": 47.5095,
//                "txid": "66d08a4cfbe844f219e7e6bdc93be70930aa1d289de7c02edfe01496bea30a3a",
//                "indexNo": 0
//        }
//                    ],
//        "outputs": [
//        {
//            "address": "DEYeybizBF9NQC8fBenrCxbVJNSkvQ5RZD",
//                "amount": 100,
//                "txid": "58feed3b4bdf4469972ae8acab6c310244b056f70ba9b0603032d4b9ea3d328a",
//                "indexNo": 0
//        },
//        {
//            "address": "DGFXZYpnaTTxm5sHGVL7KHBVp5pHd8xExy",
//                "amount": 0.010022,
//                "txid": "58feed3b4bdf4469972ae8acab6c310244b056f70ba9b0603032d4b9ea3d328a",
//                "indexNo": 1
//        }
//                    ],
//        "fee": 0.013400000000004297,
//            "is_coinbase": null
//    }
//            ]

    private String address;
    private List<NormalTransaction> normalTransactions;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<NormalTransaction> getNormalTransactions() {
        return normalTransactions;
    }

    public void setNormalTransactions(List<NormalTransaction> normalTransactions) {
        this.normalTransactions = normalTransactions;
    }

    public static class NormalTransaction extends BaseNormalTransaction implements Serializable {
        private String txid;
        private String blockHash;
        private String blockHeight;
        private String time;
        private String amount;
        private String confirmations;
        private List<myput> inputs;
        private List<myput> outputs;
        private String fee;
        private String is_coinbase;

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

        public List<myput> getInputs() {
            return inputs;
        }

        public void setInputs(List<myput> inputs) {
            this.inputs = inputs;
        }

        public List<myput> getOutputs() {
            return outputs;
        }

        public void setOutputs(List<myput> outputs) {
            this.outputs = outputs;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getIs_coinbase() {
            return is_coinbase;
        }

        public void setIs_coinbase(String is_coinbase) {
            this.is_coinbase = is_coinbase;
        }

        public static class myput implements Serializable {
            private String address;
            private String amount;
            private String txid;
            private int indexNo;

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

            public String getTxid() {
                return txid;
            }

            public void setTxid(String txid) {
                this.txid = txid;
            }

            public int getIndexNo() {
                return indexNo;
            }

            public void setIndexNo(int indexNo) {
                this.indexNo = indexNo;
            }
        }
    }


}
