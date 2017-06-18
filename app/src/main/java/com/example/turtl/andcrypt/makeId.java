package com.example.turtl.andcrypt;

/*
    Todo:
    Clear the fields incase of an invalid password
    Handle if the password fails to store

    ****Ultimately store the key for each user on a separate server (Requires java server)
    Make Java server
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

public class makeId extends AppCompatActivity {
    private String hashedPassword;
    private static File passFile;
    private static File encryptionKeyFile;
    private Hasher hasher;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_id);
        hasher = new Hasher();
    }
    //Pulls the password from the two text areas, verifies it and stores it if it is valid
    public void validifyPass(View view) throws NoSuchAlgorithmException, IOException {
        //The password objects
        EditText pwfield1 = (EditText) findViewById(R.id.password1);
        EditText pwfield2 = (EditText) findViewById(R.id.password2);
        String passWord1 = pwfield1.getText().toString();
        String passWord2 = pwfield2.getText().toString();

        //Check if the same password is entered both times
        if (!idIsValid(passWord1,passWord2)) {
            TextView pwMessage = (TextView) findViewById(R.id.passdontwork);
            pwMessage.setVisibility(View.VISIBLE);
            //Clear fields upon invalid passwords

        //If the login info passes the checks,store the password
        } else {
            //Hash and store the password
            hashPassword(passWord1);
            //If the login storage is unscucessful, display an error and exit
            if (storeLogin()) {
                //Generate a key
                //Take the user back to the homepage
                if (storeKey(hashedPassword)) returnHome();
            } else finish();
        }
    }
    //Store the login info provided by the user
    private boolean storeLogin() throws IOException {
        //Store the hashed password in a text file
        setUpFiles("pass");
        //Create objects to handle file writing
        FileWriter pw = new FileWriter(passFile);
        BufferedWriter bw = new BufferedWriter(pw);
        //Write the hashed password to the file
        bw.write(new String(hashedPassword));
        //Close resources
        bw.close();
        pw.close();
        //Return if the file was created or not
        return passFile.exists();
    }
    //Store the key
    private boolean storeKey(String key) throws IOException {
        setUpFiles("key");
        FileWriter fw = new FileWriter(encryptionKeyFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(key);
        bw.close();
        fw.close();
        return encryptionKeyFile.exists();
    }

    private boolean createKey() throws IOException, NoSuchAlgorithmException {
        Key key = new SecretKeySpec(hasher.hash(hashedPassword).getBytes(),"AES");
        this.key = key.toString();
        return storeKey(this.key);
    }

    //Set up a file in the directory path of the app and give it the name pass.txt and pubKey.txt
    private void setUpFiles(String typeSetup) {
        //Check to see if it's setting up the pass file or key file
        //Avoids null keys
        if(typeSetup.equals("pass")){
            passFile = new File(makeId.this.getApplicationContext().getFilesDir(),"pass.txt");
        } else if (typeSetup.equals("key")){
            encryptionKeyFile = new File(makeId.this.getApplicationContext().getFilesDir(),"pubKey.txt");
        }
    }

    //After the credentials are stored, return the user to the home page
    private void returnHome(){
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        //Finish the activity
        finish();
    }

    //Clear the username field when clicked on
    //Check that the password and username are valid
    private boolean idIsValid(String passWord1, String passWord2) {
        return (passWord1.equals(passWord2) && passWord1.length()>=8);

    }

    //Hash the password using the Hasher class and store it as hashedPassword
    private void hashPassword(String pass)throws UnsupportedEncodingException, NoSuchAlgorithmException {
        hashedPassword = hasher.hash(pass);
    }

//End of class
}
