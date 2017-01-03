package com.shaitan.boxopen;

import android.util.Base64;
//import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC and AESCrypt Ruby.
 * <p/>
 * Created by scottab on 04/10/2014.
 */
public final class AESCrypt {
    private  SecretKeySpec secretKey ;
    private  byte[] key ;

    private  String decryptedString;
    private  String encryptedString;

    public  void setKey(String myKey){
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");


        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public  String getDecryptedString(){
        return decryptedString;
    }
    public  void setDecryptedString(String decryptedString){
        this.decryptedString = decryptedString;
    }
    public  String getEncryptedString(){
        return encryptedString;
    }
    public  void setEncryptedString(String encryptedString){
        this.encryptedString = encryptedString;
    }
    public  String encrypt(String strToEncrypt)    {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            setEncryptedString(new String(Base64.encode(strToEncrypt.getBytes(), Base64.NO_WRAP)));
        }catch (Exception e){
            System.out.println("Error while encrypting: "+e.toString());
        }
        return null;
    }
    public  String decrypt(String strToDecrypt){
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decode(strToDecrypt.getBytes(), Base64.NO_WRAP))));

        }catch (Exception e){
            System.out.println("Error while decrypting: "+e.toString());
        }
        return null;
    }
}