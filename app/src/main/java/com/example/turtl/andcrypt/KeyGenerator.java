package com.example.turtl.andcrypt;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by turtl on 12/7/2016.
 */

public class KeyGenerator {
    //The hashed password to hash again and make a way for the key
    private String text;
    static File encryptionKeyFile;
    String key;
    //Constructor
    public KeyGenerator(String text) {
    //Hash the hashed password and store it as the text
    Hasher hash = new Hasher();
        try {
            this.text = hash.hash(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
    }
    //Generate the key
    public boolean createKey() {
        Key key = new SecretKeySpec(text.getBytes(),"AES");
        this.key = key.toString();
        return storeKey(this.key);

    }
    public void setUpFile() {
        makeId mi = new makeId();
        encryptionKeyFile = new File(mi.getApplicationContext().getFilesDir(),"pubKey.txt");
    }



    private boolean storeKey(String key) {
        setUpFile();
        FileWriter fw = null;
        BufferedWriter bw;
        try {
            fw = new FileWriter(encryptionKeyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);
        try {
            bw.write(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUpFile();
        return encryptionKeyFile.exists();
    }


}
