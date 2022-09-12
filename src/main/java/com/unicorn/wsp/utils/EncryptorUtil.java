package com.unicorn.wsp.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptorUtil {
    private static StandardPBEStringEncryptor encryptor;
    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("unicorn");
    }

    // 加密
    public static String encrypt(String text){
        return encryptor.encrypt(text);
    }

    // 解密，传入密文
    public static String decrypt(String ciphertext){
        return encryptor.decrypt(ciphertext);
    }
}