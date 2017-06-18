package com.example.turtl.andcrypt;
/*
    Todo:
    Create a java server or find an online database to
    store the login info in
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static com.example.turtl.andcrypt.R.string.alertDialog;

public class MainActivity extends AppCompatActivity {
    //The file containing the hash
    File passFile;
    //Context of the app
    Context context;
    //The hashed password from the file
    String passHash;
    TextView tv;
    //Number of password tries
    private int numTries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numTries = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpContext();
        setupFile(context);
        //Checks if the password already exists
        if (!isExistingUser()){
            //If the user hasn't used the app before, takes them to the makeID activity
            Intent createIntent = new Intent(this, makeId.class);
            startActivity(createIntent);
            //Finish the activity
            finish();
        }
        //Load the hashed password
        try {
            loadPassHash();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //End of the onCreate function
    }
    //Loads the password into the passHash variable
    public void loadPassHash() throws FileNotFoundException {
        tv = (TextView) findViewById(R.id.viewer);
        Scanner myScan = new Scanner(passFile);
        String text = null;
        while (myScan.hasNext()) {
            text=myScan.nextLine();
        }
        //If there is something in the file
        if (text!=null) {
            passHash=text;
            //tv.setText(passHash);
            myScan.close();
        } else {
            finish();
        }

    }
    //Set up the file
    public void setupFile(Context c) {
        passFile = new File(c.getFilesDir(),"pass.txt");
    }
    //Set up the context to be used in the file
    public void setUpContext() {
        context = getApplicationContext();
    }
    //Check if the user has a password or not
    public boolean isExistingUser() {
        //See if the file exists
        return passFile.exists();
    }
    //Start the intent to go to the selectimages page
    private void startImages() {
        Intent startImages = new Intent(this,selectImages.class);
        startActivity(startImages);
        finish();
    }
    //Function to submit the passwords
    public void submitLogin(View view) throws FileNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //Hasher variable
        Hasher hasher = new Hasher();
        //Hash the entered password
        String enteredHash = hasher.hash(getEnteredPass());
        //Check the entered hash equals the hash stored
        if (passesMatch(enteredHash)) {
            startImages();
        }
        //Increment the number of tries
        numTries++;
        if (numTries>2 && numTries <5) {
            //Display warning
            showAlert(numTries);
        } else if (numTries > 5) {
            //Kill the process tree and end the program
            android.os.Process.killProcess(android.os.Process.myPid());

        }
        //End of function
    }
    //Show an alert when the amount of incorrect tries is above 3
    private void showAlert(int numTries) {
        AlertDialog attemptWarning = new AlertDialog.Builder(this).create();
    attemptWarning.setTitle("Login Attempt Warning");
    attemptWarning.setMessage("Only " + (6-numTries) + " more attempts are available");
    attemptWarning.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });

    attemptWarning.show();

}
    //Check if the password enetered matches the password in the file
    public boolean passesMatch(String userPass) {
        return passHash.equals(userPass);
    }
    //Get the entered password from the textview
    public String getEnteredPass() {
        EditText enteredText = (EditText) findViewById(R.id.userEnteredPass);
        return enteredText.getText().toString();
    }
}
