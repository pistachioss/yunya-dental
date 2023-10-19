package com.yunya.framework.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class DESUtils {
    private final static String DES = "DES";

    public static void main(String args[]) throws Exception {
        String salt = "12345678";
        String id = "1000";
        String str1 = encrypt(id, salt);
        System.out.println(str1);
        String str2 = decrypt(str1, salt);
        System.out.println(str2);
    }

    // 加密
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        DESKeySpec ds = new DESKeySpec(key);
        SecureRandom sr = new SecureRandom();
        SecretKeyFactory skfactory = SecretKeyFactory.getInstance(DES);
        SecretKey skey = skfactory.generateSecret(ds);
        cipher.init(Cipher.ENCRYPT_MODE, skey, sr);
        byte[] b = cipher.doFinal(data);
        return b;
    }

    // 加密
    public static String encrypt(String data, String key) throws Exception {
        return byte2hex(encrypt(data.getBytes(), key.getBytes()));
    }

    // 解密
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        DESKeySpec ds = new DESKeySpec(key);
        SecureRandom sr = new SecureRandom();
        SecretKeyFactory skfactory = SecretKeyFactory.getInstance(DES);
        SecretKey skey = skfactory.generateSecret(ds);
        cipher.init(Cipher.DECRYPT_MODE, skey, sr);
        return cipher.doFinal(data);
    }

    // 解密
    public static String decrypt(String data, String key) throws Exception {
        return new String(decrypt(hex2byte(data.getBytes()), key.getBytes()));
    }

    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException();
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
}