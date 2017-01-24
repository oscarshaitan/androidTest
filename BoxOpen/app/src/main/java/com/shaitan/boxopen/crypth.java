package com.shaitan.boxopen;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import com.shaitan.boxopen.AESCrypt;



/**
 * Created by Shaitan on 3/11/2016.
 */
public class crypth {
    AESCrypt AES = new AESCrypt();
    //AES aes = new AES();
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public String AES_Encrypt(String KEY, String message) throws GeneralSecurityException {
        AES.setKey(KEY);
        AES.encrypt(message.trim());
        String messageEncrypted = AES.getEncryptedString();
        return messageEncrypted;
    }
    public String AES_Decrypt(String KEY, String message) throws GeneralSecurityException {
        AES.setKey(KEY);
        AES.decrypt(message.trim());
        String messageDecrypted = AES.getDecryptedString();
        return messageDecrypted;
    }

   /* public String AES_E() throws Exception {
        return aes.bytesToHex(aes.encrypt("hola"));
    }
    public String AES_D() throws Exception {
        return new String(aes.decrypt("02e56fb3df2507fdb14d347f0589e5a5"));
    }*/





}
