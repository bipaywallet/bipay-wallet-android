package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class MyUSDTRecord implements Serializable {

//    {
//        "code": 0,
//            "message": "success",
//            "data": [
//        {
//            "txid": "012ddb26e7c47c4e72cf7f6ee2d11ce4b0c7e764218e538f07bb714ba8c8fced",
//                "blockHeight": 543425,
//                "blockHash": "00000000000000000008fa3875641d9c74c39e91e885c755d76e39a309b4d815",
//                "sendingAddress": "13uoEs2puQV85WaGQXqe3rANugK8sxQTxj",
//                "referenceAddress": "1DZhpKnNAjEwoFq3pgKSJjbJyD1Pcib4r1",
//                "blockTime": 1538125326,
//                "amount": 2905,
//                "fee": 0.00005113,
//                "type": "Simple Send",
//                "typeInt": 0,
//                "propertyId": 31,
//                "valid": null
//        }
//    ]
//    }

    private String txid;
    private String blockHash;
    private String blockHeight;
    private String sendingAddress;
    private String referenceAddress;
    private String blockTime;
    private String amount;
    private String fee;
    private String type;
    private long typeInt;
    private long propertyId;
    private String valid;

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

    public String getSendingAddress() {
        return sendingAddress;
    }

    public void setSendingAddress(String sendingAddress) {
        this.sendingAddress = sendingAddress;
    }

    public String getReferenceAddress() {
        return referenceAddress;
    }

    public void setReferenceAddress(String referenceAddress) {
        this.referenceAddress = referenceAddress;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(long typeInt) {
        this.typeInt = typeInt;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }


}
