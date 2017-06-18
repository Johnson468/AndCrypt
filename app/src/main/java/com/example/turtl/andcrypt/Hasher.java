package com.example.turtl.andcrypt;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jordan on 12/4/2016.
 */
    //Class to handle hashing passwords using SHA 256
public class Hasher {

    public Hasher() {

    }
    //Method to hash and returned hashed texts
    public String hash(String text) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuffer hexString = new StringBuffer();
        for (byte i : hash) {
            String hex = Integer.toHexString(0xff & i);
            if (hex.length()==1) { hexString.append(0);}
            hexString.append(hex);
        }
        return hexString.toString();
    }
    //End of class
}
