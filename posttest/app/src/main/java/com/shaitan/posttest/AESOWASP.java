package com.shaitan.posttest;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Basic symmetric encryption example
 */
public class AESOWASP {

    public void test() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, ShortBufferException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {



        byte[] input = "TRUE".getBytes();

        byte[] keyBytes = "USER1IMEITEST00000000000".getBytes();

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

        //System.out.println(new String(input));

        // encryption pass

        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);

        ctLength += cipher.doFinal(cipherText, ctLength);

        System.out.println(new String(cipherText).getBytes("UTF-8").toString());

        //System.out.println(ctLength);

        // decryption pass

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);

        ptLength += cipher.doFinal(plainText, ptLength);

        System.out.println(new String(plainText));

        //System.out.println(ptLength);
    }
}