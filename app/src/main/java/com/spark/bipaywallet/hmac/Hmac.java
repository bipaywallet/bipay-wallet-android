package com.spark.bipaywallet.hmac;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {

    public Hmac() {
    }

    public String hmacSHA512(String data, String key) {
        String result = "";
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            final byte[] macData = mac.doFinal(data.getBytes());
            result = byteArrayToHexString(macData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }


}
